package minipython.builder.transform.cbuilderManualTests;

import minipython.builder.lang.MPyModule;
import minipython.builder.cbuilder.Transform;
import minipython.builder.lang.Call;
import minipython.builder.lang.Expression;
import minipython.builder.lang.Statement;
import minipython.builder.lang.literal.BoolLiteral;
import minipython.builder.lang.literal.IntLiteral;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.lang.variables.VariableDeclaration;

import static minipython.builder.lang.builtins.Builtins.FUNCTION_PRINT;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test that transformation to cbuilder works correctly,
 * for the cbuilder boolean test.
 */
public class BooleanTest {

    /**
     * Mini-Python code of program generated:
     * ```python
     * a
     * b
     *
     * a = b
     * d = -30
     *
     * type(a)
     * type(d)
     *
     * idA = id(a)
     * print(idA)
     *
     * print(id(a))
     * id(print(d))
     * id(type(a))
     *
     * e = id(type(print(d)))
     * e = print(-50)
     * ```
     */
    static void generateProgram(Path output) {
        List<Statement> body = new LinkedList<>();
        Set<VariableDeclaration> globalVars = new HashSet<>();
        MPyModule builder = new MPyModule(body, globalVars);

        VariableDeclaration a = new VariableDeclaration("a");
        globalVars.add(a);
        body.add(new Assignment(a, new BoolLiteral(true)));

        VariableDeclaration b = new VariableDeclaration("b");
        globalVars.add(b);
        body.add(new Assignment(b, new BoolLiteral(false)));

        body.add(new Call(FUNCTION_PRINT, List.of(new Expression[] {
           a,
           b,
        })));
        body.add(new Call(FUNCTION_PRINT, List.of(new Expression[] {
            a,
        })));
        body.add(new Call(FUNCTION_PRINT, List.of(new Expression[] {
            b,
        })));
        body.add(new Call(FUNCTION_PRINT, List.of(new Expression[] {
            a,
        })));
        body.add(new Call(FUNCTION_PRINT, List.of(new Expression[] {
            b,
        })));

        Transform.transform(builder).writeProgram(output);
    }

    public static void main(String[] args) {
        Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/TransformBooleanTestCBuilder/");
        generateProgram(fileOutput);
    }
}
