package minipython.builder.lang.conditions;

import java.util.List;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;

public record ConditionalBlock(
    Expression condition,
    List<Statement> body
) {

    public ConditionalBlock(Expression condition, Statement[] body) {
        this(condition, List.of(body));
    }
}
