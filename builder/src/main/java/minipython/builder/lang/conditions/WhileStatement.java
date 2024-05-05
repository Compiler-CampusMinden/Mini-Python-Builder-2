package minipython.builder.lang.conditions;

import java.util.List;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;

public record WhileStatement(
    ConditionalBlock statement
) implements Statement {
    public WhileStatement(Expression condition, List<Statement> body) {
        this(new ConditionalBlock(condition, body));
    }
}
