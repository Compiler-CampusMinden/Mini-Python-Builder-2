package minipython.builder.wasm.lang.literal;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_STR_STATIC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;
import minipython.builder.wasm.lang.Module.StringToken;

/**
 * A string literal.
 *
 * String creation happens via \a Module#newString.
 *
 * @see Module#newString
 */
public record StringLiteral(
    String value,
    StringToken token
) implements Expression {

	@Override
	public BlockContent buildExpression(Module partOf) {
        if (partOf != token.owner) {
            throw new IllegalArgumentException("mismatch between owning module and calling module");
        }

        partOf.declareRuntimeImport(MPY_OBJ_INIT_STR_STATIC);

        return new Block(
            "start of string literal",
            "end of string literal",
            "",
            new Block(
                "  ",
                // get the pointer to the cstr allocated in
                // the c-runtime module's memory
                new Line("global.get $%s".formatted(token.identifier)),
                new Line("call $__mpy_obj_init_str_static")
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
