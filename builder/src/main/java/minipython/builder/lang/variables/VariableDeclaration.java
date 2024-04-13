package minipython.builder.lang.variables;

import minipython.builder.lang.Expression;

public record VariableDeclaration(
    String name
) implements Expression {
}
