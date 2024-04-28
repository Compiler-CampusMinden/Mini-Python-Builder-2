package minipython.builder.lang.variables;

import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;

public record Assignment(
    VariableDeclaration lhs,
    Expression rhs
) implements Statement {
}
