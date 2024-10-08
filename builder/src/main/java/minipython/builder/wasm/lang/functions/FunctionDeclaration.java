package minipython.builder.wasm.lang.functions;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_ARGS_MALLOCED;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_ARGS_GET_POSITIONAL;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_ARGS_INIT_MALLOCED;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_FUNC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_OBJECT;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_INC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_RETURN;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.Scope;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.lang.Statement;

public class FunctionDeclaration implements Expression {

    private final StringLiteral name;

    private final Scope scope;

    private final List<Statement> body;

    private final List<VariableDeclaration> arguments;

    private final Set<VariableDeclaration> localVariables;

    public FunctionDeclaration(StringLiteral name, List<VariableDeclaration> arguments, Set<VariableDeclaration> localVariables, List<Statement> body, Scope scope) {
        this.name = name;
        this.arguments = arguments;
        this.localVariables = localVariables;
        this.body = body;
        this.scope = scope;
    }

    public FunctionDeclaration(StringLiteral name, List<Statement> body) {
        this(name, List.of(), Set.of(), body);
    }

    public FunctionDeclaration(StringLiteral name, List<VariableDeclaration> arguments, Set<VariableDeclaration> localVariables, List<Statement> body) {
        this(name, arguments, localVariables, body, Scope.SCOPE_GLOBAL);
    }

    public Scope scope() {
        return scope;
    }

    public List<Statement> body() {
        return body;
    }

    public List<VariableDeclaration> arguments() {
        return arguments;
    }

    public Set<VariableDeclaration> localVariables() {
        return localVariables;
    }

    public String name() {
        return name.value();
    }

    public StringLiteral nameLiteral() {
        return name;
    }

    @Override
    public BlockContent buildExpression(MPyModule partOf) {
        // only valid for global functions,
        // since local (e.g. class bound) functions
        // don't have a variable they're
        // (i.e. their miniopython object) stored in
        assert(scope == Scope.SCOPE_GLOBAL);
        return new Line("global.get $%s".formatted(name.value()));
    }

    public BlockContent buildRawFuncDeclaration(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_INIT_OBJECT, MPY_OBJ_RETURN);
        return new Block(
            "",
            // required so that the init code can take a reference to the function
            // (cf. 'ref.func incorrectly gives "Undeclared function reference"'
            // https://github.com/WebAssembly/wasp/issues/59)
            new Line("(elem declare func $%s)".formatted(name.value())),
            new Line("(func $%s (param $args i32) (param $kwargs i32) (result i32)".formatted(name.value())),
            new Block(
                "  ",
                // 1. locals
                // 1.1 builder internal locals
                new Line("(local $argHelper i32)"),
                // 1.2 arguments
                new Block(Optional.empty(), arguments.stream().map(arg -> arg.buildDeclaration(partOf)).collect(Collectors.toList()), Optional.empty(), ""),
                // 1.3 local variables
                new Block(Optional.empty(), localVariables.stream().map(var -> var.buildDeclaration(partOf)).collect(Collectors.toList()), Optional.empty(), ""),
                // 2. argument extraction
                buildArgumentExtraction(partOf),
                // 3. local variables initialisation (null -> None)
                new Block(Optional.empty(), localVariables.stream().map(var -> var.buildInitialisation(partOf)).collect(Collectors.toList()), Optional.empty(), ""),
                // explicit return statements:
                // put the return value on the stack,
                // call __mpy_obj_return on it,
                // and unconditionally jump to earlyReturn
                // (i.e. the end of the block).
                // After the block,
                // - first cleanup happens
                // - then the value that was on top of the stack,
                //    before the jump, is returned
                new Line("(block $earlyReturn (result i32)"),
                new Block("  ",
                    // 4. function body
                    new Block(Optional.empty(), body.stream().map(s -> s.buildStatement(partOf)).collect(Collectors.toList()), Optional.empty(), ""),
                    // 5. create implicit/automatic return value
                    new Line("call $__mpy_obj_init_object"),
                    new Line("call $__mpy_obj_return")
                ),
                new Line(")"),
                // 5. cleanup (not implemented yet)
                // 5.1 arguments
                new Block(Optional.empty(), arguments.stream().map(arg -> arg.buildCleanup(partOf)).collect(Collectors.toList()), Optional.empty(), ""),
                // 5.2 declared variables
                new Block(Optional.empty(), localVariables.stream().map(var -> var.buildCleanup(partOf)).collect(Collectors.toList()), Optional.empty(), "")
                // 6. return value
                // (implicit, i.e. the value put on the stack inside the earlyReturn
                // function body block)
            ),
            new Line(")")
        );
    }

    private BlockContent buildArgumentExtraction(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_ARGS_INIT_MALLOCED, MPY_ARGS_GET_POSITIONAL, MPY_ARGS_MALLOCED);

        List<BlockContent> argExtraction = new LinkedList<>();

        argExtraction.add(new Block("argHelper creation start", "argHelper created", "",
            name.buildExpressionCString(partOf),
            new Line("local.get $args"),
            new Line("local.get $kwargs"),
            new Line("i32.const %d".formatted(arguments.size())),
            new Line("call $__mpy_args_init_malloced"),
            new Line("local.set $argHelper")
        ));

        int pos = 0;
        for (VariableDeclaration arg : arguments) {
            argExtraction.add(new Block("arg %s start".formatted(arg.name()), "arg %s end".formatted(arg.name()), "",
                new Line("local.get $argHelper"),
                new Line("i32.const %d".formatted(pos)),
                arg.nameLiteral().buildExpressionCString(partOf),
                new Line("call $__mpy_args_get_positional"),
                new Line("local.set $%s".formatted(arg.name()))
            ));
            pos++;
        }

        argExtraction.add(new Line("local.get $argHelper"));
        argExtraction.add(new Line("call $__mpy_args_finish_malloced"));

        return new Block(Optional.empty(), argExtraction, Optional.empty(), "");
    }

    public BlockContent buildFuncObjDeclaration(MPyModule partOf) {
        // only valid for global functions,
        // since local (e.g. class bound) functions
        // don't have a variable they're
        // (i.e. their miniopython object) stored in
        assert(scope == Scope.SCOPE_GLOBAL);
        return new Line("(global $%s (mut i32) (i32.const 0))".formatted(name.value()));
    }

    public BlockContent buildInitialisation(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_INIT_FUNC, MPY_OBJ_REF_INC);

        // if the function is global,
        // save it in the respective global variable.
        // Otherwise (e.g. class bound)
        // simply leave its minipython object on the stack
        // after creation.
        Block saveGlobal;
        if (scope == Scope.SCOPE_GLOBAL) {
            saveGlobal = new Block(
                "",
                new Line("global.set $%s".formatted(name.value())),
                new Line("global.get $%s".formatted(name.value())),
                new Line("call $__mpy_obj_ref_inc")
            );
        } else {
            saveGlobal = new Block("");
        }

        return new Block(
            "",
            new Line("ref.func $%s".formatted(name.value())),
            new Line("i32.const 1"),
            new Line("table.grow $__mpy_runtime_fn_table"),
            // grow returns old size;
            // since new size is old size + 1
            // and table index is 0-based,
            // old size is actually the index of the new element
            new Line("call $__mpy_obj_init_func"),
            saveGlobal
        );
    }

    @Override
    public BlockContent buildStatement(MPyModule partOf) {
        // referencing a function delcaration as a statement
        // is simply a no-op
        return new Line("", "function '%s'".formatted(name.value()));
    }

}
