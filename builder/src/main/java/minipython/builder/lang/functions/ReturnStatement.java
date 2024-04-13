package minipython.builder.lang.functions;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;

public record ReturnStatement(
    Expression value
) implements Statement {
}
