package minipython.builder.lang.literal;

import java.util.List;

import minipython.builder.lang.Expression;

/**
 * A MiniPython Tuple object.
 *
 * @param elements values of this tuple; in order
 */
public record TupleLiteral(
    List<Expression> elements
) implements Expression {

    public TupleLiteral(Expression... elements) {
        this(List.of(elements));
    }
}

