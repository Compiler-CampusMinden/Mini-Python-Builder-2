package minipython.builder.cbuilder.run.manualTests;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.ProgramBuilder;
import minipython.builder.cbuilder.lang.Reference;
import minipython.builder.cbuilder.lang.Statement;
import minipython.builder.cbuilder.lang.literals.BoolLiteral;
import minipython.builder.cbuilder.lang.literals.IntLiteral;
import minipython.builder.cbuilder.lang.objects.*;
import minipython.builder.cbuilder.lang.objects.functions.Argument;
import minipython.builder.cbuilder.lang.objects.functions.Function;
import minipython.builder.cbuilder.lang.variables.Assignment;
import minipython.builder.cbuilder.lang.variables.VariableDeclaration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Allows to test the Builder without involving the ast or anything.
 *
 * TODO remove this when manual testing of this is not needed anymore
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
        ProgramBuilder builder = new ProgramBuilder();

        builder.addVariable(new VariableDeclaration("a"));
        builder.addStatement(new Assignment(new Reference("a"), new BoolLiteral(true)));

        builder.addVariable(new VariableDeclaration("b"));
        builder.addStatement(new Assignment(new Reference("b"), new BoolLiteral(false)));

        builder.addStatement(new Call(new Reference("print"), List.of(new Expression[] {
           new Reference("a"),
           new Reference("b"),
        })));
        builder.addStatement(new Call(new Reference("print"), List.of(new Expression[] {
            new Reference("a"),
            })));
        builder.addStatement(new Call(new Reference("print"), List.of(new Expression[] {
            new Reference("b"),
            })));
        builder.addStatement(new Call(new Reference("print"), List.of(new Expression[] {
            new Reference("a"),
            })));
        builder.addStatement(new Call(new Reference("print"), List.of(new Expression[] {
            new Reference("b"),
            })));

        builder.writeProgram(output);
    }

    public static void main(String[] args) {
        Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/BooleanTest/");
        generateProgram(fileOutput);
    }
}
