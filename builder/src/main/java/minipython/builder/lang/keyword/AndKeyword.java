package minipython.builder.lang.keyword;

import minipython.builder.lang.Expression;

public record AndKeyword(
    Expression left,
    Expression right
) implements Expression {
}
