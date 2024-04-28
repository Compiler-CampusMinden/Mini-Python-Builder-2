package minipython.builder.lang.object;

import java.util.List;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;

public record SuperCall(
    List<Expression> positionalArgs
) implements Statement {

    public SuperCall(Expression... positionalArgs) {
        this(List.of(positionalArgs));
    }

    public SuperCall() {
        this(List.of());
    }
}
