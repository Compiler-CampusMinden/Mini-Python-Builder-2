package minipython.builder.cbuilder.lang.keywords.bool;

import minipython.builder.cbuilder.lang.Expression;

/**
 * A boolean and operation (Python/MiniPython).
 *
 * @see BinaryBoolKeyword How to utilise this to generate code.
 */
public class AndKeyword extends BinaryBoolKeyword {

    /**
     * Create a new boolean and operation.
     *
     * @param x The operations left expression.
     * @param y The operations right expression.
     */
    public AndKeyword(Expression x, Expression y) {
        super("&&", x, y);
    }

}
