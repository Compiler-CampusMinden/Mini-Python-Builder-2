package minipython.builder.lang.conditions;

import minipython.builder.lang.Statement;

public record WhileStatement(
    ConditionalBlock statement
) implements Statement {
}
