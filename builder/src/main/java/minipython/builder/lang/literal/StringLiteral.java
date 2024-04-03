package minipython.builder.lang.literal;

import minipython.builder.lang.Expression;

/**
 * A MiniPython string object.
 */
public record StringLiteral(
    String value
) implements Expression {
}

