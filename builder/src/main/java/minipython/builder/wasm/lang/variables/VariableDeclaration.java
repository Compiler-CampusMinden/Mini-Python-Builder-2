package minipython.builder.wasm.lang.variables;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_OBJECT;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_INC;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;
import minipython.builder.wasm.lang.Module.VariableToken;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.functions.FunctionDeclaration.FunctionVariableToken;
import minipython.builder.wasm.lang.literal.StringLiteral;

/**
 * A variable declaration; in contrast to MiniPython itself, variables must be declared explicitly before assignment/referencing them.
 *
 * New global variables are created with \a {@link Module#newVariable(String)}.
 *
 * @see Module#newVariable(String)
 */
public class VariableDeclaration implements Expression {

    private final StringLiteral name;

    private final Object token;

    private final boolean isGlobal;

    /**
     * Create a new global variable declaration.
     *
     * Create new variables with \a {@link Module#newVariable(String)}.
     *
     * @see Module#newVariable(String)
     */
    public VariableDeclaration(StringLiteral name, VariableToken token) {
        this.token = token;
        this.name = name;
        this.isGlobal = true;
    }

    /**
     * Create a new local variable declaration.
     *
     * Create new variables for arguments with \a {@link FunctionDeclaration#addArgument(StringLiteral)}.
     * Create new variables for local variables with \a {@link FunctionDeclaration#addLocalVariable(StringLiteral)}.
     *
     * @see FunctionDeclaration#addArgument(StringLiteral)
     * @see FunctionDeclaration#addLocalVariable(StringLiteral)
     */
    public VariableDeclaration(StringLiteral name, FunctionVariableToken token) {
        this.token = token;
        this.name = name;
        this.isGlobal = false;
    }

    protected boolean isGlobal() {
        return this.isGlobal;
    }

    protected String kind() {
        return this.isGlobal ? "global" : "local";
    }

    public String name() {
        return name.value();
    }

    public StringLiteral nameLiteral() {
        return name;
    }

    @Override
    public BlockContent buildExpression(Module partOf) {
        return new Line("%s.get $%s".formatted(kind(), name.value()));
    }

    public BlockContent buildDeclaration(Module partOf) {
        if (isGlobal) {
            return new Line("(global $%s (mut i32) (i32.const 0))".formatted(name.value()));
        } else {
            return new Line("(local $%s i32)".formatted(name.value()));
        }
    }

    public BlockContent buildInitialisation(Module partOf) {
        partOf.declareRuntimeImports(MPY_OBJ_INIT_OBJECT, MPY_OBJ_REF_INC);

        return new Block(
            "",
            // TODO(FW): init new None here, instead of plain object()
            new Line("call $__mpy_obj_init_object"),
            new Line("%s.set $%s".formatted(kind(), name.value())),
            new Line("%s.get $%s".formatted(kind(), name.value())),
            new Line("call $__mpy_obj_ref_inc")
        );
    }

    public BlockContent buildCleanup(Module partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_REF_DEC);
        return new Block(
            "",
            new Line("%s.get $%s".formatted(kind(), name.value())),
            new Line("call $__mpy_obj_ref_dec")
        );
    }

    @Override
    public BlockContent buildStatement(Module partOf) {
        // referencing a variable delcaration as a statement
        // is simply a no-op
        return new Line("", "%s variable '%s'".formatted(kind(), name.value()));
    }

}
