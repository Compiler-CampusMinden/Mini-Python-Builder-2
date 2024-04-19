package minipython.builder.wasm.lang.functions;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_FUNC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_OBJECT;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_INC;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;
import minipython.builder.wasm.lang.Module.FunctionToken;
import minipython.builder.wasm.lang.Statement;

public class FunctionDeclaration implements Expression {

    private final String name;

    private final Object token;

    private final List<Statement> body;

    public FunctionDeclaration(FunctionToken token, String name, List<Statement> body) {
        this.name = name;
        this.token = token;
        this.body = body;
    }

    public String name() {
        return name;
    }

    @Override
    public BlockContent buildExpression(Module partOf) {
        return new Line("global.get $%s".formatted(name));
    }

    public BlockContent buildRawFuncDeclaration(Module partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_INIT_OBJECT);
        return new Block(
            "",
            // required so that the init code can take a reference to the function
            // (cf. 'ref.func incorrectly gives "Undeclared function reference"'
            // https://github.com/WebAssembly/wasp/issues/59)
            new Line("(elem declare func $%s)".formatted(name)),
            new Line("(func $%s (param $args i32) (param $kwargs i32) (result i32)".formatted(name)),
            new Block(
                Optional.empty(),
                body.stream().map(s -> s.buildStatement(partOf)).collect(Collectors.toList()),
                Optional.empty(),
                "  "
            ),
            new Line("  call $__mpy_obj_init_object"),
            new Line(")")
        );
    }

    public BlockContent buildFuncObjDeclaration(Module partOf) {
        return new Line("(global $%s (mut i32) (i32.const 0))".formatted(name));
    }

    public BlockContent buildInitialisation(Module partOf) {
        partOf.declareRuntimeImports(MPY_OBJ_INIT_FUNC, MPY_OBJ_REF_INC);

        return new Block(
            "",
            new Line("ref.func $%s".formatted(name)),
            new Line("i32.const 1"),
            new Line("table.grow $__mpy_runtime_fn_table"),
            // grow returns old size;
            // since new size is old size + 1
            // and table index is 0-based,
            // old size is actually the index of the new element
            new Line("call $__mpy_obj_init_func"),
            new Line("global.set $%s".formatted(name)),
            new Line("global.get $%s".formatted(name)),
            new Line("call $__mpy_obj_ref_inc")
        );
    }

    @Override
    public BlockContent buildStatement(Module partOf) {
        // referencing a function delcaration as a statement
        // is simply a no-op
        return new Line("", "function '%s'".formatted(name));
    }

}
