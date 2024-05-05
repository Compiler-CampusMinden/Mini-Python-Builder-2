package minipython.builder.wasm.lang.literal;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_STR_STATIC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;

/**
 * A string literal.
 */
public class StringLiteral implements Expression {

    private final String value;
    private String identifier;

    public StringLiteral(String value) {
        this.value = value;
        this.identifier = null;
    }

    public String value() {
        return value;
    }

    public String getIdentifier(MPyModule partOf) {
        // identifier hasn't been retrieved yet,
        // create new one
        if (identifier == null) {
            identifier = partOf.nextStringIdentifier();
        }
        return identifier;
    }

	@Override
	public BlockContent buildExpression(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_INIT_STR_STATIC);

        return new Block(
            "start of string literal",
            "end of string literal",
            "",
            new Block(
                "  ",
                // get the pointer to the cstr allocated in
                // the c-runtime module's memory
                new Line("global.get $%s".formatted(getIdentifier(partOf))),
                new Line("call $__mpy_obj_init_str_static")
            )
        );
	}

    public BlockContent buildExpressionCString(MPyModule partOf) {
        return new Line("global.get $%s".formatted(getIdentifier(partOf)));
    }

	@Override
	public BlockContent buildStatement(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_REF_DEC);
        return new Block(
            "",
            buildExpression(partOf),
            new Line("call $__mpy_obj_ref_dec")
        );
	}
}
