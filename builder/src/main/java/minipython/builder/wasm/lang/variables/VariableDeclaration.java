package minipython.builder.wasm.lang.variables;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_OBJECT;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_INC;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.Scope;
import minipython.builder.wasm.lang.literal.StringLiteral;

/**
 * A variable declaration; in contrast to MiniPython itself, variables must be declared explicitly before assignment/referencing them.
 */
public class VariableDeclaration implements Expression {

    private final StringLiteral name;

    private final Scope scope;

    /**
     * Create a new variable declaration.
     */
    public VariableDeclaration(StringLiteral name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }

    /**
     * Create a new <b>global</b> variable declaration.
     */
    public VariableDeclaration(StringLiteral name) {
        this(name, Scope.SCOPE_GLOBAL);
    }

    protected boolean isGlobal() {
        return this.scope == Scope.SCOPE_GLOBAL;
    }

    protected String kind() {
        return this.isGlobal() ? "global" : "local";
    }

    public String name() {
        return name.value();
    }

    public StringLiteral nameLiteral() {
        return name;
    }

    @Override
    public BlockContent buildExpression(MPyModule partOf) {
        return new Line("%s.get $%s".formatted(kind(), name.value()));
    }

    public BlockContent buildDeclaration(MPyModule partOf) {
        if (isGlobal()) {
            return new Line("(global $%s (mut i32) (i32.const 0))".formatted(name.value()));
        } else {
            return new Line("(local $%s i32)".formatted(name.value()));
        }
    }

    public BlockContent buildInitialisation(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_INIT_OBJECT, MPY_OBJ_REF_INC);

        return new Block(
            "",
            // TODO(FW): init new None here, instead of plain object()
            new Line("call $__mpy_obj_init_object"),
            new Line("%s.set $%s".formatted(kind(), name.value())),
            new Line("%s.get $%s".formatted(kind(), name.value())),
            new Line("call $__mpy_obj_ref_inc")
        );
    }

    public BlockContent buildCleanup(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_REF_DEC);
        return new Block(
            "",
            new Line("%s.get $%s".formatted(kind(), name.value())),
            new Line("call $__mpy_obj_ref_dec")
        );
    }

    @Override
    public BlockContent buildStatement(MPyModule partOf) {
        // referencing a variable delcaration as a statement
        // is simply a no-op
        return new Line("", "%s variable '%s'".formatted(kind(), name.value()));
    }

}
