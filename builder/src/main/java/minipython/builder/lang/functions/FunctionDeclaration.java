package minipython.builder.lang.functions;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;
import minipython.builder.lang.variables.VariableDeclaration;

public record FunctionDeclaration(
    String name,
    List<VariableDeclaration> arguments,
    Set<VariableDeclaration> localVariables,
    List<Statement> body
) implements Expression {
}
