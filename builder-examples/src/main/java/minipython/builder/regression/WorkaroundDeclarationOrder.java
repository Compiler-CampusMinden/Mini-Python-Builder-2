package minipython.builder.regression;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.wasm.run.WasmtimeCliRunner;

public class WorkaroundDeclarationOrder {

    /**
     * Ensure that even trough transformations,
     * the declaration order of functions is honored,
     * so that functions referring to each other don't break in the cbuilder.
     * (Which requires that a function is declared before the functions
     * that use it.)
     */
    public static void main(String[] args) throws Exception {
        StringLiteral sSecond = new StringLiteral("second");

        FunctionDeclaration funcSecond = new FunctionDeclaration(
            "second",
            List.of(),
            Set.of(),
            List.of(new Call(
                Builtins.FUNCTION_PRINT,
                List.of(sSecond)
            ))
        );

        FunctionDeclaration funcFirst = new FunctionDeclaration(
            "first",
            List.of(),
            Set.of(),
            List.of(new Call(
                funcSecond,
                List.of()
            ))
        );

        // the CBuilder currently requires the function to be declared
        // before using it in other functions.
        // Use a LinkedHashSet which guarantees the encounter order to be equal to the insertion order,
        // ensuring funcSecond is declared first.
        LinkedHashSet<FunctionDeclaration> functions = new LinkedHashSet<>();
        functions.add(funcSecond);
        functions.add(funcFirst);

        MPyModule mod = new MPyModule(
            List.of(
                new Call(
                    funcFirst,
                    List.of()
                )
            ),
            Set.of(),
            Set.of(),
            functions
        );

        new WasmtimeCliRunner().run(minipython.builder.wasm.Transform.transform(mod).build());

        java.nio.file.Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/Regression_WorkaroundDeclarationOrder/");
        minipython.builder.cbuilder.Transform.transform(mod).writeProgram(fileOutput);
    }

}
