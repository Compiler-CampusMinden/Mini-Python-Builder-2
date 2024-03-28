package minipython.builder.wasm.lang;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_CALL;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;

import java.util.List;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.literal.TupleLiteral;

/**
 * A MiniPython call, ie a function or constructor call.
 *
 * @param callable function called or type instantiated.
 * @param positionalArgs positional arguments to this function call.
 */
public record Call(
    Expression callable,
    List<Expression> positionalArgs
) implements Expression {

	@Override
	public BlockContent buildExpression(Module partOf) {
        partOf.declareRuntimeImport(MPY_CALL);

        TupleLiteral positionalArgsParam = new TupleLiteral(positionalArgs);

        return new Block("start of __mpy_call", "end of __mpy_call", "",
            new Line("", "callable (first arg)"),
            callable.buildExpression(partOf),
            new Line("", "positional args in a tuple (second arg)"),
            positionalArgsParam.buildExpression(partOf),
            new Line("i32.const 0", "keyword args (third arg)"),
            new Line("call $__mpy_call")
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
