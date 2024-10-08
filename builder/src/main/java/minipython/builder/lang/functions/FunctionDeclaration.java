package minipython.builder.lang.functions;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Scope;
import minipython.builder.lang.Statement;
import minipython.builder.lang.variables.VariableDeclaration;

public record FunctionDeclaration(
    String name,
    List<VariableDeclaration> arguments,
    Set<VariableDeclaration> localVariables,
    List<Statement> body,
    Scope scope
) implements Expression {

    public FunctionDeclaration(String name, List<VariableDeclaration> arguments, Set<VariableDeclaration> localVariables, List<Statement> body) {
        this(name, arguments, localVariables, body, Scope.SCOPE_GLOBAL);
    }

    public FunctionDeclaration(String name, List<Statement> body) {
        this(name, List.of(), Set.of(), body);
    }

    public boolean equals(Object other) {
        return this == other;
    }

    public int hashCode() {
        // unoverriden hashCode, which does not recurse into record's attributes
        return System.identityHashCode(this);
    }
}
