package minipython.builder.wasm.lang.builtin;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_BUILTINS_GET_FN_PRINT;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;

/**
 * The built-in print function, as a MiniPython function object.
 */
public class FunctionPrint implements Expression {

	@Override
	public BlockContent buildExpression(Module partOf) {
        partOf.declareRuntimeImport(MPY_BUILTINS_GET_FN_PRINT);
        return new Line("call $__mpy_builtins_get_fn_print");
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
