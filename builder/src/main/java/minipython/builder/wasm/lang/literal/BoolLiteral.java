package minipython.builder.wasm.lang.literal;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_BOOLEAN;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;

/**
 * A MiniPython int object.
 *
 * @param value value of this int object
 */
public record BoolLiteral(
    boolean value
) implements Expression {

    @Override
    public BlockContent buildExpression(Module partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_INIT_BOOLEAN);

        return new Block(
            "start of boolean literal",
            "end of boolean literal",
            "",
            new Block(
                "  ",
                new Line("i32.const %d".formatted(value ? 1 : 0)),
                new Line("call $__mpy_obj_init_boolean")
            )
        );
    }

    @Override
    public BlockContent buildStatement(Module partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_REF_DEC);
        return new Block(
            "",
            buildExpression(partOf),
            new Line("call $__mpy_obj_ref_dec")
        );
    }
}

