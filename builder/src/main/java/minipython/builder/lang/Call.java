package minipython.builder.lang;

import java.util.List;

/**
 * A MiniPython call, ie a function or constructor call.
 *
 * @param callable function called or type instantiated.
 * @param positionalArgs positional arguments to this function call.
 */
public record Call(
    Expression callable,
    List<Expression> positionalArgs
) implements Expression {
}
