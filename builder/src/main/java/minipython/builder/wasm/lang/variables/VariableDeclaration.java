package minipython.builder.wasm.lang.variables;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_OBJECT;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_INC;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;
import minipython.builder.wasm.lang.Module.VariableToken;

/**
 * A variable declaration; in contrast to MiniPython itself, variables must be declared explicitly before assignment/referencing them.
 *
 * New global variables are created with \a {@link Module#newVariable(String)}.
 *
 * @see Module#newVariable(String)
 */
public class VariableDeclaration implements Expression {

    private final String name;

    private final Object token;

    /**
     * Create a new global variable declaration.
     *
     * Create new variables with \a {@link Module#newVariable(String)}.
     *
     * @see Module#newVariable(String)
     */
    public VariableDeclaration(String name, VariableToken token) {
        this.token = token;
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public BlockContent buildExpression(Module partOf) {
        return new Line("global.get $%s".formatted(name));
    }

    public BlockContent buildDeclaration(Module partOf) {
        return new Line("(global $%s (mut i32) (i32.const 0))".formatted(name));
    }

    public BlockContent buildInitialisation(Module partOf) {
        partOf.declareRuntimeImports(MPY_OBJ_INIT_OBJECT, MPY_OBJ_REF_INC);

        return new Block(
            "",
            // TODO(FW): init new None here, instead of plain object()
            new Line("call $__mpy_obj_init_object"),
            new Line("global.set $%s".formatted(name)),
            new Line("global.get $%s".formatted(name)),
            new Line("call $__mpy_obj_ref_inc")
        );
    }

    @Override
    public BlockContent buildStatement(Module partOf) {
        // referencing a variable delcaration as a statement
        // is simply a no-op
        return new Line("", "variable '%s'".formatted(name));
    }

}
