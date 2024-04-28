package minipython.builder.transform.cbuilderManualTests;

import minipython.builder.lang.MPyModule;
import minipython.builder.cbuilder.Transform;
import minipython.builder.lang.Call;
import minipython.builder.lang.Statement;
import minipython.builder.lang.literal.IntLiteral;
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
public class IntOps {

    /**
     * Mini-Python code of program generated:
     * ```python
     * a
     *
     * a = 10
     *
     * print(a ^ 12)
     *
     * ```
     */
    static void generateProgram(Path output) {
        List<Statement> body = new LinkedList<>();
        Set<VariableDeclaration> globalVars = new HashSet<>();
        MPyModule builder = new MPyModule(body, globalVars);

        VariableDeclaration varA = new VariableDeclaration("a");
        globalVars.add(varA);

        body.add(new Assignment(varA, new IntLiteral(10)));
        body.add(
            new Call(
                FUNCTION_PRINT,
                new Call(
                    new AttributeReference(varA, "__xor__"),
                    new IntLiteral(12)
                )
            )
        );

        Transform.transform(builder).writeProgram(output);
    }

    public static void main(String[] args) {
        Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/TransformIntOpsCBuilder/");
        generateProgram(fileOutput);
    }
}

