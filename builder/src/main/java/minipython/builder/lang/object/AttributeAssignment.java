package minipython.builder.lang.object;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;

public record AttributeAssignment(
    AttributeReference attribute,
    Expression value
) implements Statement {
}
