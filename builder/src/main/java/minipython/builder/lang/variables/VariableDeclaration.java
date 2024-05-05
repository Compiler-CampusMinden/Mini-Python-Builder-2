package minipython.builder.lang.variables;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Scope;

public record VariableDeclaration(
    String name,
    Scope scope
) implements Expression {

    public VariableDeclaration(String name) {
        this(name, Scope.SCOPE_GLOBAL);
    }
}
