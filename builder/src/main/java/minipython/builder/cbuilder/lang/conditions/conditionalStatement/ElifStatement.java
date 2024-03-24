package minipython.builder.cbuilder.lang.conditions.conditionalStatement;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.Statement;
import minipython.builder.cbuilder.lang.conditions.IfThenElseStatement;

import java.util.List;

/**
 * An elif block (Python/MiniPython) of a conditional statement.
 *
 * @see IfThenElseStatement How to utilise this to generate code.
 */
public class ElifStatement extends ConditionalStatement {

    /**
     * Create a new elif block.
     *
     * @param condition The condition of the elif block.
     * @param body The list of statements in the body of the elif block.
     */
    public ElifStatement(Expression condition,
                         List<Statement> body) {
        super("else if", condition, body);
    }
}
