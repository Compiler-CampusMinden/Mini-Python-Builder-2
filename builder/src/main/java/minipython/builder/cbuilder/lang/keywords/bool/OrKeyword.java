package minipython.builder.cbuilder.lang.keywords.bool;

import minipython.builder.cbuilder.lang.Expression;

/**
 * A boolean or operation (Python/MiniPython).
 *
 * @see BinaryBoolKeyword How to utilise this to generate code.
 */
public class OrKeyword extends BinaryBoolKeyword {

    /**
     * Create a new boolean or operation.
     *
     * @param x The operations left expression.
     * @param y The operations right expression.
     */
    public OrKeyword(Expression x, Expression y) {
        super("||", x, y);
    }

}
