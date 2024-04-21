package minipython.builder.wasm.lang.object;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_GET_ATTR;

import java.util.Optional;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;
import minipython.builder.wasm.lang.literal.StringLiteral;

public record AttributeReference(
    Expression object,
    StringLiteral attributeName
) implements Expression {

    @Override
    public BlockContent buildExpression(Module partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_GET_ATTR);
        return new Block(
            "start of attribute reference to '%s'".formatted(attributeName.value()),
            "end of attribute reference to '%s'".formatted(attributeName.value()),
            "",
            new Block(
                "  ",
                new Block(
                    Optional.of("object (first arg)"),
                    Optional.empty(),
                    "",
                    new Block("  ", object.buildExpression(partOf))
                ),
                new Block(
                    Optional.of("attribute name (second arg)"),
                    Optional.empty(),
                    "",
                    new Block("  ", attributeName.buildExpressionCString(partOf))
                ),
                new Line("call $__mpy_obj_get_attr")
            )
        );
    }

    @Override
    public BlockContent buildStatement(Module partOf) {
        return new Block("",
            buildExpression(partOf),
            new Line("drop")
        );
    }

}
