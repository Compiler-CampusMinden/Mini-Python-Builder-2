package minipython.builder.wasm.lang.builtin;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_BUILTINS_GET_FN_PRINT;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;

/**
 * The built-in print function, as a MiniPython function object.
 */
public class FunctionPrint implements Expression {

	@Override
	public BlockContent buildExpression(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_BUILTINS_GET_FN_PRINT);
        return new Line("call $__mpy_builtins_get_fn_print");
	}

	@Override
	public BlockContent buildStatement(MPyModule partOf) {
        // referencing a builtin as a statement
        // is simply a no-op
        return new Line("", "builtin print()");
	}

}
