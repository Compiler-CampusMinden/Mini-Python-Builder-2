package minipython.builder.wasm.lang;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_STR_ALLOC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_STR_INTO_CSTR;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_STR_SET;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.object.MPyClass;
import minipython.builder.wasm.lang.variables.VariableDeclaration;

/**
 * The top-level element of a MiniPython program.
 */
public class MPyModule {
    private final List<Statement> body;
    private Set<RuntimeImport> importedRuntimeFunctions = new HashSet<>();
    private List<StringLiteral> strings = new LinkedList<>();
    private Set<VariableDeclaration> variables = new HashSet<>();
    private Set<FunctionDeclaration> functions = new HashSet<>();
    private Set<MPyClass> classes = new HashSet<>();

    public final BuiltinStrings BUILTIN_STRINGS = new BuiltinStrings(this);

    public class BuiltinStrings {
        public final StringLiteral ATTR_FUNC_BOOL;

        private BuiltinStrings(MPyModule owner) {
            ATTR_FUNC_BOOL = owner.newString("__bool__");
        }
    }

    /**
     * This class is used to enforce string creation via \a newString.
     *
     * This allows to easily create unique identifiers for each string.
     */
    public class StringToken{
        /**
         * Module wide unique identifier of the string.
         */
        public final String identifier;
        /**
         * Tracks which module created this instance.
         */
        public final MPyModule owner;

        private StringToken(String identifier, MPyModule owner) {
            this.identifier = identifier;
            this.owner = owner;
        }
    }

    public class VariableToken {
        public final MPyModule owner;

        private VariableToken(MPyModule owner) {
            this.owner = owner;
        }

    }

    public class FunctionToken {
        public final MPyModule owner;

        private FunctionToken(MPyModule owner) {
            this.owner = owner;
        }

    }

    public class ClassToken {
        public final MPyModule owner;

        private ClassToken(MPyModule owner) {
            this.owner = owner;
        }
    }

    public int getMemoryOffsetStringSection() {
        return 0;
    }

    /**
     * Create a new string literal.
     */
    public StringLiteral newString(String value) {
        StringLiteral literal = new StringLiteral(value, new StringToken("string" + strings.size(), this));
        strings.add(literal);
        return literal;
    }

    public VariableDeclaration newVariable(StringLiteral name) {
        VariableDeclaration var = new VariableDeclaration(name, new VariableToken(this));
        variables.add(var);
        return var;
    }

    public FunctionDeclaration newFunction(StringLiteral name, List<Statement> body) {
        FunctionDeclaration func = new FunctionDeclaration(new FunctionToken(this), name, body);
        functions.add(func);
        return func;
    }

    public MPyClass newClass(StringLiteral name, Expression parent, Map<StringLiteral, Expression> classAttributes) {
        MPyClass clazz = new MPyClass(new ClassToken(this), name, parent, classAttributes);
        classes.add(clazz);
        return clazz;
    }

    /**
     * @param body statements in the global scope of the MiniPython program
     */
    public MPyModule(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return this.body;
    }

    /**
     * (Internal) \a functionNames are used by generated WASM code and must be imported.
     */
    public void declareRuntimeImport(RuntimeImport... functionNames) {
        this.importedRuntimeFunctions.addAll(Arrays.asList(functionNames));
    }

    /**
     * Build the WASM code corresponding to the program represented by this instance.
     */
    public String build() {
        int stringMemoryOffset = 0;

        BlockContent body = body();
        BlockContent init = init(stringMemoryOffset);
        BlockContent initStringWasmFn = initStringWasmFn();
        StringConstants stringData = stringConstants(stringMemoryOffset);
        List<BlockContent> functionRawBodysDeclaration = functions.stream().map(f -> f.buildRawFuncDeclaration(this)).collect(Collectors.toList());
        List<BlockContent> classRawTypesDeclaration = classes.stream().map(c -> c.buildRawTypeDeclaration(this)).collect(Collectors.toList());
        List<BlockContent> variablesDeclaration = variables.stream().map(v -> v.buildDeclaration(this)).collect(Collectors.toList());
        List<BlockContent> functionObjectsDeclaration = functions.stream().map(f -> f.buildFuncObjDeclaration(this)).collect(Collectors.toList());
        List<BlockContent> classObjectsDeclaration = classes.stream().map(c -> c.buildTypeObjDeclaration(this)).collect(Collectors.toList());
        // this must be called last,
        // as the imports are collected when building
        // the statements/expressions
        //
        // DO NOT call buildXXX() functions below this point,
        // for the same reason.
        BlockContent imports = imports();

        return new Block("",
            new Line("(module"),
            new Block(
                "  ",
                // Ordering: orient from what clang currently generates (or how wasm2wat shows what clang generated) for the c-runtime wasm-lib, i.e:
                // (function signature types, for whatever reason)
                // 1. imports
                imports,
                // 2. functions
                // 2.1 main (global statements)
                new Block(
                    Optional.of("this function represents execution of statements in global (mini)python scope"),
                    Optional.empty(),
                    "",
                    new Line("(func (export \"mpy__main__\")"),
                    body,
                    new Line(")")
                ),
                // 2.2 global functions
                new Block(Optional.empty(), functionRawBodysDeclaration, Optional.empty(), ""),
                // 2.3 class functions/methods
                new Block(Optional.empty(), classRawTypesDeclaration, Optional.empty(), ""),
                // 2.4 init
                new Block(
                    Optional.of("this function is responsible for one-time initialisation tasks"),
                    Optional.empty(),
                    "",
                    new Line("(func (export \"_initialize\")"),
                    init,
                    new Line(")")
                ),
                // 2.5 builder supplied functions (e.g. string init fn)
                initStringWasmFn,
                // 3. tables (declaration)
                // 4. memory (declaration)
                new Line("(memory 1)"), // TODO(FW): this reserves 1 page (64kb) - dynamically calcualte how much is needed
                // 5. globals
                // 5.1 global functions (minipython objects)
                new Block(Optional.empty(), functionObjectsDeclaration, Optional.empty(), ""),
                // 5.2 classes (minipython objects)
                new Block(Optional.empty(), classObjectsDeclaration, Optional.empty(), ""),
                // 5.3 strings (char* pointers)
                stringData.globals(),
                // 5.4 variables
                new Block(Optional.empty(), variablesDeclaration, Optional.empty(), ""),
                // 6. exports
                // 7. table content (indirect function calls)
                // 8. memory content / static data (e.g. strings)
                stringData.data()
            ),
            new Line(")")
        ).toString("");
    }

    /**
     * @return WAT import statements.
     */
    private BlockContent imports() {
        List<BlockContent> imports = new ArrayList<>();
        for (RuntimeImport fn : importedRuntimeFunctions) {
            imports.add(new Line(fn.build()));
        }

        imports.add(new Line("(import \"mpy_runtime\" \"__indirect_function_table\" (table $__mpy_runtime_fn_table 0 funcref))"));

        return new Block(Optional.of("imports from c-runtime library"), imports, Optional.empty(), "");
    }

    /**
     * @return A WAT function body, containing the statements of the global scope of this program.
     */
    private BlockContent body() {
        List<BlockContent> body = new ArrayList<>();

        for (Statement stmt : this.body) {
            body.add(stmt.buildStatement(this));
        }

        return new Block(Optional.empty(), body, Optional.empty(), "  ");
    }

    private BlockContent init(int stringMemoryOffset) {
        return new Block("  ",
            stringInit(stringMemoryOffset),
            variableInit(),
            functionInit(),
            classInit()
        );
    }

    private BlockContent initStringWasmFn() {
        declareRuntimeImport(MPY_STR_ALLOC, MPY_STR_SET, MPY_STR_INTO_CSTR);

        return new Block("",
            new Line("(func $init_string (param $stringLocation i32) (param $stringLength i32) (result i32)"),
            new Block("  ",
                new Line("(local $string i32)"),
                new Line("(local $i i32)"),
                new Line(""),
                new Line("local.get $stringLength"),
                new Line("call $__mpy_str_alloc"),
                new Line("local.set $string"),
                new Line(""),
                new Line("local.get $stringLocation"),
                new Line("local.set $i"),
                new Block("",
                    new Line("loop"),
                    new Block("  ",
                        new Line("local.get $string"),
                        new Line(""),
                        new Line("local.get $i"),
                        new Line("local.get $stringLocation"),
                        new Line("i32.sub"),
                        new Line(""),
                        new Line("local.get $i"),
                        new Line("i32.load"),
                        new Line(""),
                        new Line("call $__mpy_str_set"),
                        new Line(""),
                        new Line("local.get $i"),
                        new Line("i32.const 1"),
                        new Line("i32.add"),
                        new Line("local.tee $i"),
                        new Line(""),
                        new Line("local.get $stringLength"),
                        new Line("local.get $stringLocation"),
                        new Line("i32.add"),
                        new Line("i32.lt_s"),
                        new Line("br_if 0")
                    ),
                    new Line("end")
                ),
                new Line(""),
                new Line("local.get $string"),
                new Line("call $__mpy_str_into_cstr")
            ),
            new Line(")")
        );
    }

    private BlockContent stringInit(int stringMemoryOffset) {
        List<BlockContent> body = new ArrayList<>();

        int offset = stringMemoryOffset;

        for (StringLiteral lit : strings) {
            // add +1 to string length for null-byte
            body.add(new Block(
                        "start of string init",
                        "end of string inti",
                        "",
                        new Line("i32.const %d".formatted(offset)),
                        new Line("i32.const %d".formatted(lit.value().length() + 1)),
                        new Line("call $init_string"),
                        new Line("global.set $%s".formatted(lit.token().identifier))
            ));
            offset += lit.value().length() + 1;
        }

        return new Block(
                "start of strings init",
                "end of string init",
                "",
                new Block(Optional.empty(), body, Optional.empty(), "  ")
        );
    }

    private BlockContent variableInit() {
        return new Block(
            "start of variables init",
            "end of variables init",
            "",
            new Block(
                Optional.empty(),
                variables.stream().map(v -> v.buildInitialisation(this)).collect(Collectors.toList()),
                Optional.empty(),
                "  "
            )
        );
    }

    private BlockContent functionInit() {
        return new Block(
            "start of functions init",
            "end of functions init",
            "",
            new Block(
                Optional.empty(),
                functions.stream().map(f -> f.buildInitialisation(this)).collect(Collectors.toList()),
                Optional.empty(),
                "  "
            )
        );
    }

    private BlockContent classInit() {
        return new Block(
            "start of classes init",
            "end of classes init",
            "",
            new Block(
                Optional.empty(),
                classes.stream().map(c -> c.buildInitialisation(this)).collect(Collectors.toList()),
                Optional.empty(),
                "  "
            )
        );
    }

    private record StringConstants(
        BlockContent globals,
        BlockContent data
    ) {
    }

    // string memory + globals
    private StringConstants stringConstants(int stringMemoryOffset) {
        List<BlockContent> globals = new LinkedList<>();

        StringBuilder strings = new StringBuilder();
        strings.append("(data (i32.const %d) \"".formatted(stringMemoryOffset));

        for (StringLiteral lit : this.strings) {
            strings.append(lit.value());
            strings.append("\\00");

            globals.add(new Line("(global $%s (mut i32) (i32.const 0))".formatted(lit.token().identifier)));
        }

        strings.append("\")");

        return new StringConstants(
            new Block(Optional.empty(), globals, Optional.empty(), ""),
            new Line(strings.toString())
        );
    }
}
