package minipython.builder.transform;

import java.util.ArrayList;
import java.util.List;

import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.cbuilder.Transform;
import minipython.builder.lang.Call;
import minipython.builder.lang.Expression;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.Statement;

/**
 * A simple demonstration of the generic builder,
 * that simply prints the string "Hello World";
 * transforming the program to C-code utilising the c-runtime.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * print("Hello World")
 * ```
 */
public class PrintStringCbuilder {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        MPyModule mod = new MPyModule(module);

        module.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new StringLiteral("Hello World")
            })
        ));

        java.nio.file.Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/Transform_PrintStringCbuilder/");
        Transform.transform(mod).writeProgram(fileOutput);
    }

}
