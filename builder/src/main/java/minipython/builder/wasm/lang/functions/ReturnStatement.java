package minipython.builder.wasm.lang.functions;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_RETURN;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.Statement;

public record ReturnStatement(
    Expression value
) implements Statement {

	@Override
	public BlockContent buildStatement(MPyModule partOf) {
        partOf.declareRuntimeImports(MPY_OBJ_RETURN);
        return new Block(
            "start of return",
            "end of return",
            "",
            new Block(
                "  ",
                value.buildExpression(partOf),
                new Line("call $__mpy_obj_return"),
                new Line("br $earlyReturn")
            )
        );
	}
}
