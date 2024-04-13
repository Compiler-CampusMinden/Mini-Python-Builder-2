package minipython.builder.lang.object;

import minipython.builder.lang.Expression;

public record AttributeReference(
    Expression object,
    String attributeName
) implements Expression {
}
