package minipython.builder.lang.literal;

import minipython.builder.lang.Expression;

/**
 * A MiniPython int object.
 *
 * @param value value of this int object
 */
public record IntLiteral(
    long value
) implements Expression {
}
