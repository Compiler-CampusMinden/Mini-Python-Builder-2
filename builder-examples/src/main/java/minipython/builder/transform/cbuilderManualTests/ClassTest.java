package minipython.builder.transform.cbuilderManualTests;

import minipython.builder.lang.MPyModule;
import minipython.builder.cbuilder.Transform;
import minipython.builder.lang.Call;
import minipython.builder.lang.Statement;
import minipython.builder.lang.literal.BoolLiteral;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.lang.object.AttributeReference;
import minipython.builder.lang.object.MPyClass;
import minipython.builder.lang.object.SuperCall;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.lang.variables.VariableDeclaration;

import static minipython.builder.lang.builtins.Builtins.FUNCTION_PRINT;
import static minipython.builder.lang.builtins.Builtins.CLASS_MPY_OBJECT;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test that transformation to cbuilder works correctly,
 * for the cbuilder class test.
 */
public class ClassTest {

    /**
     * Mini-Python code of program generated:
     * ```python
     * b
     *
     * def foo():
     *   print("foo")
     *
     * class A:
     *   def __init__(self):
     *     super()
     *   def foo(self):
     *     foo()
     *
     * b = A()
     * b.foo()
     * ```
     */
    static void generateProgram(Path output) {
        List<Statement> body = new LinkedList<>();
        Set<VariableDeclaration> globalVars = new HashSet<>();
        Set<MPyClass> classes = new HashSet<>();
        Set<FunctionDeclaration> funcs = new HashSet<>();
        MPyModule builder = new MPyModule(body, globalVars, classes, funcs);

        VariableDeclaration varB = new VariableDeclaration("b");
        globalVars.add(varB);

        FunctionDeclaration funcFoo = new FunctionDeclaration(
            "foo",
            List.of(),
            Set.of(),
            List.of(new Statement[]{
                new Call(
                    FUNCTION_PRINT,
                    new StringLiteral("foo")
                )
            })
        );
        funcs.add(funcFoo);

        MPyClass clazz = new MPyClass(
            "A",
            CLASS_MPY_OBJECT,
            Set.of(new FunctionDeclaration[] {
                new FunctionDeclaration(
                    "foo",
                    List.of(new VariableDeclaration[]{new VariableDeclaration("self")}),
                    Set.of(),
                    List.of(new Statement[]{
                        new Call(funcFoo)
                    })
                ),
                new FunctionDeclaration(
                    "__init__",
                    List.of(new VariableDeclaration[]{new VariableDeclaration("self")}),
                    Set.of(),
                    List.of(new Statement[]{
                        new SuperCall()
                    })
                ),
            }),
            Map.of()
        );
        classes.add(clazz);

        body.add(new Assignment(varB, new Call(clazz)));
        body.add(new Call(new AttributeReference(varB, "foo")));

        Transform.transform(builder).writeProgram(output);
    }

    public static void main(String[] args) {
        Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/TransformClassTestCBuilder/");
        generateProgram(fileOutput);
    }
}

