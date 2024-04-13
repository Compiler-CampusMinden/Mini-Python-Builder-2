package minipython.builder.transform.cbuilderManualTests;

import minipython.builder.lang.Module;
import minipython.builder.cbuilder.Transform;
import minipython.builder.lang.Call;
import minipython.builder.lang.Statement;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.lang.object.AttributeReference;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.lang.variables.VariableDeclaration;

import static minipython.builder.lang.builtins.Builtins.FUNCTION_PRINT;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Test that transformation to cbuilder works correctly,
 * for the cbuilder int ops test.
 */
public class StrOps {

    /**
     * Mini-Python code of program generated:
     * ```python
     * a
     *
     * a = "abc"
     *
     * print(a == "abc")
     *
     * ```
     */
    static void generateProgram(Path output) {
        List<Statement> body = new LinkedList<>();
        Set<VariableDeclaration> globalVars = new HashSet<>();
        Module builder = new Module(body, globalVars);

        VariableDeclaration varA = new VariableDeclaration("a");
        globalVars.add(varA);

        body.add(new Assignment(varA, new StringLiteral("abc")));
        body.add(
            new Call(
                FUNCTION_PRINT,
                new Call(
                    new AttributeReference(varA, "__eq__"),
                    new StringLiteral("abc")
                )
            )
        );

        Transform.transform(builder).writeProgram(output);
    }

    public static void main(String[] args) {
        Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/TransformStrOpsCBuilder/");
        generateProgram(fileOutput);
    }
}
