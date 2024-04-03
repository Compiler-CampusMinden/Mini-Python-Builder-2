package minipython.builder.wasm.lang.literal;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_INT;
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
public record IntLiteral(
    long value
) implements Expression {

	@Override
	public BlockContent buildExpression(Module partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_INIT_INT);

        return new Block(
            "start of int literal",
            "end of int literal",
            "",
            new Block(
                "  ",
                new Line("i64.const %d".formatted(value)),
                new Line("call $__mpy_obj_init_int")
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
