package minipython.builder.wasm.lang;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_STR_ALLOC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_STR_INTO_CSTR;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_STR_SET;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.literal.StringLiteral;

/**
 * The top-level element of a MiniPython program.
 */
public class Module {
    private final List<Statement> body;
    private Set<RuntimeImport> importedRuntimeFunctions = new HashSet<>();
    private List<StringLiteral> strings = new LinkedList<>();

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
        public final Module owner;

        private StringToken(String identifier, Module owner) {
            this.identifier = identifier;
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

    /**
     * @param body statements in the global scope of the MiniPython program
     */
    public Module(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return this.body;
    }

    /**
     * (Internal) \a functionName is used by generated WASM code and must be imported.
     */
    public void declareRuntimeImport(RuntimeImport functionName) {
        this.importedRuntimeFunctions.add(functionName);
    }

    /**
     * (Internal) \a functionNames are used by generated WASM code and must be imported.
     */
    public void declareRuntimeImports(RuntimeImport... functionNames) {
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
        BlockContent stringData = stringConstants(stringMemoryOffset);
        // this must be called last,
        // as the imports are collected when building
        // the statements/expressions
        BlockContent imports = imports();

        return new Block("",
            new Line("(module"),
            new Block(
                "  ",
                imports,
                new Block(
                    Optional.of("this function represents execution of statements in global (mini)python scope"),
                    Optional.empty(),
                    "",
                    new Line("(func (export \"mpy__main__\")"),
                    body,
                    new Line(")")
                ),
                new Block(
                    Optional.of("this function is responsible for one-time initialisation tasks"),
                    Optional.empty(),
                    "",
                    new Line("(func (export \"_initialize\")"),
                    init,
                    new Line(")")
                ),
                initStringWasmFn,
                new Line("(memory 1)"), // TODO(FW): this reserves 1 page (64kb) - dynamically calcualte how much is needed
                stringData
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
        return new Block("  ", stringInit(stringMemoryOffset));
    }

    private BlockContent initStringWasmFn() {
        declareRuntimeImports(MPY_STR_ALLOC, MPY_STR_SET, MPY_STR_INTO_CSTR);

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

    // string memory + globals
    private BlockContent stringConstants(int stringMemoryOffset) {
        List<BlockContent> constants = new LinkedList<>();

        StringBuilder strings = new StringBuilder();
        strings.append("(data (i32.const %d) \"".formatted(stringMemoryOffset));

        for (StringLiteral lit : this.strings) {
            strings.append(lit.value());
            strings.append("\\00");

            constants.add(new Line("(global $%s (mut i32) (i32.const 0))".formatted(lit.token().identifier)));
        }

        strings.append("\")");

        constants.add(new Line(strings.toString()));

        return new Block(Optional.empty(), constants, Optional.empty(), "");
    }
}
