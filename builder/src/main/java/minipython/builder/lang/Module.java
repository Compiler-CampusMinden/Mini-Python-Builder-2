package minipython.builder.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.object.MPyClass;
import minipython.builder.lang.variables.VariableDeclaration;

/**
 * The top-level element of a MiniPython program.
 */
public record Module(
    List<Statement> body,
    Set<VariableDeclaration> globalVariabes,
    Set<MPyClass> classes,
    Set<FunctionDeclaration> functions
) {

    public Module(List<Statement> body) {
        this(body, Set.of());
    }

    public Module(List<Statement> body, Set<VariableDeclaration> globalVariables) {
        this(body, globalVariables, Set.of());
    }

    public Module(List<Statement> body, Set<VariableDeclaration> globalVariables, Set<MPyClass> classes) {
        this(body, globalVariables, classes, Set.of());
    }
}
