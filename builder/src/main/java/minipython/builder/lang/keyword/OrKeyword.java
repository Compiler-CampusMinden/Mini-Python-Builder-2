package minipython.builder.lang.keyword;

import minipython.builder.lang.Expression;

public record OrKeyword(
    Expression left,
    Expression right
) implements Expression {
}

