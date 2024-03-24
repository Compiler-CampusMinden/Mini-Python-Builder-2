package minipython.builder.cbuilder.lang.conditions.conditionalStatement;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.Statement;
import minipython.builder.cbuilder.lang.conditions.IfThenElseStatement;

import java.util.List;

/**
 * An if block (Python/MiniPython) of a conditional statement.
 *
 * @see IfThenElseStatement How to utilise this to generate code.
 */
public class IfStatement extends ConditionalStatement {

    /**
     * Create a new if block.
     *
     * @param condition The condition of the if block.
     * @param body The list of statements in the body of the if block.
     */
    public IfStatement(Expression condition,
                       List<Statement> body) {
        super("if", condition, body);
    }

}
