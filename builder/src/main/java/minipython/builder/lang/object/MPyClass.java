package minipython.builder.lang.object;

import java.util.Map;
import java.util.Set;

import minipython.builder.lang.Expression;
import minipython.builder.lang.functions.FunctionDeclaration;

public record MPyClass(
    String name,
    Expression parent,
    Set<FunctionDeclaration> functions,
    Map<String, Expression> classAttributes
) implements Expression {
}
