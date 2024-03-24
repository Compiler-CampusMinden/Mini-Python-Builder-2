package minipython.builder.cbuilder.run.manualTests;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.ProgramBuilder;
import minipython.builder.cbuilder.lang.Reference;
import minipython.builder.cbuilder.lang.literals.IntLiteral;
import minipython.builder.cbuilder.lang.literals.StringLiteral;
import minipython.builder.cbuilder.lang.objects.AttributeReference;
import minipython.builder.cbuilder.lang.objects.Call;
import minipython.builder.cbuilder.lang.variables.Assignment;
import minipython.builder.cbuilder.lang.variables.VariableDeclaration;

import java.nio.file.Path;
import java.util.List;

public class StrOps {
    static void generateProgram(Path output) {
        ProgramBuilder builder = new ProgramBuilder();

        VariableDeclaration varADecl = new VariableDeclaration("a");
        Reference varA = new Reference("a");
        Reference funcPrint = new Reference("print");

        builder.addVariable(varADecl);
        builder.addStatement(new Assignment(varA, new StringLiteral("abc")));

        builder.addStatement(new Call(funcPrint, List.of(new Expression[]{
            new Call(new AttributeReference("__eq__", varA), List.of(new Expression[]{
                new StringLiteral("abc"),
                }))
        })));

        builder.writeProgram(output);
    }

    public static void main(String[] args) {
        Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/StrOps/");
        generateProgram(fileOutput);
    }
}
