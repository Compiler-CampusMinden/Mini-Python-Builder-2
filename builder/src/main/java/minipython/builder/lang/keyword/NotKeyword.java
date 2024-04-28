package minipython.builder.lang.keyword;

import minipython.builder.lang.Expression;

public record NotKeyword(
    Expression e
) implements Expression {
}

