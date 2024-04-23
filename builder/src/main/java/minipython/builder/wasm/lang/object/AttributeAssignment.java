package minipython.builder.wasm.lang.object;

import java.util.List;
import java.util.Optional;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;
import minipython.builder.wasm.lang.Statement;

public record AttributeAssignment(
    AttributeReference attribute,
    Expression value
) implements Statement {

    @Override
    public BlockContent buildStatement(Module partOf) {
        return new Block(
            "start of assignment to '%s'".formatted(attribute.name()),
            "start of assignment to '%s'".formatted(attribute.name()),
            "",
            new Block(
                "  ",
                new Block(
                    Optional.of("object the attribute belongs to (first arg)"),
                    List.of(attribute.object().buildExpression(partOf)),
                    Optional.empty(),
                    ""
                ),
                new Block(
                    Optional.of("name of the attribute (second arg)"),
                    List.of(attribute.attributeName().buildExpressionCString(partOf)),
                    Optional.empty(),
                    ""
                ),
                new Block(
                    Optional.of("value of the attribute (second arg)"),
                    List.of(value.buildExpression(partOf)),
                    Optional.empty(),
                    ""
                ),
                new Line("call $__mpy_obj_set_attr"),
                // set_attr returns the object the attr was set on,
                // but it's not needed here anymore
                new Line("drop")
            )
        );
    }
}
