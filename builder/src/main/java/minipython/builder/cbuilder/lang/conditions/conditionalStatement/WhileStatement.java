package minipython.builder.cbuilder.lang.conditions.conditionalStatement;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.Statement;
import minipython.builder.cbuilder.lang.conditions.IfThenElseStatement;

import java.util.List;

/**
 * An while loop with condition and statements.
 */
public class WhileStatement extends ConditionalStatement implements Statement {

    /**
     * Create a new while loop.
     *
     * @param condition The condition of the while loop.
     * @param body The list of statements in the body of the while loop.
     */
    public WhileStatement(Expression condition,
                          List<Statement> body) {
        super("while", condition, body);
    }

    @Override
    public String buildStatement() {
        return this.build();
    }
}
