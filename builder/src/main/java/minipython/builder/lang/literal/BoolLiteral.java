package minipython.builder.lang.literal;

import minipython.builder.lang.Expression;

/**
 * A MiniPython bool object.
 *
 * @param value value of this bool object
 */
public record BoolLiteral(
    boolean value
) implements Expression {
}
