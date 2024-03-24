package minipython.builder.cbuilder.run.manualTests;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.ProgramBuilder;
import minipython.builder.cbuilder.lang.Reference;
import minipython.builder.cbuilder.lang.literals.StringLiteral;
import minipython.builder.cbuilder.lang.objects.AttributeReference;
import minipython.builder.cbuilder.lang.objects.Call;
import minipython.builder.cbuilder.lang.objects.MPyClass;
import minipython.builder.cbuilder.lang.objects.SuperCall;
import minipython.builder.cbuilder.lang.objects.functions.Argument;
import minipython.builder.cbuilder.lang.objects.functions.Function;
import minipython.builder.cbuilder.lang.variables.Assignment;
import minipython.builder.cbuilder.lang.variables.VariableDeclaration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ClassTest {
    static void generateProgram(Path output) {
        ProgramBuilder builder = new ProgramBuilder();
        VariableDeclaration varBDecl = new VariableDeclaration("b");
        Reference varB = new Reference("b");
        builder.addVariable(varBDecl);

        Reference funcPrint = new Reference("print");
        Reference object = new Reference("__MPyType_Object");

        MPyClass clazz = new MPyClass("A", object, List.of(new Function[]{
            new Function("foo", List.of(new Expression[]{
                new Call(funcPrint, List.of(new Expression[]{
                    new StringLiteral("foo")
                }))
            }), List.of(new Argument[]{new Argument("self", 0)}), List.of()),
            new Function("__init__", List.of(new Expression[]{
                new SuperCall(List.of())
            }), List.of(new Argument[]{new Argument("self", 0)}), List.of())
        }), Map.of());
        builder.addClass(clazz);
        builder.addStatement(new Assignment(varB, new Call(new Reference("A"), List.of())));
        builder.addStatement(new Call(new AttributeReference("foo", varB), List.of()));


        builder.writeProgram(output);
    }

    public static void main(String[] args) {
        Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/ClassTest/");
        generateProgram(fileOutput);
    }
}
