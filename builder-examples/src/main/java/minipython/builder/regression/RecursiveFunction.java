package minipython.builder.regression;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.Scope;
import minipython.builder.lang.Statement;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.conditions.ConditionalBlock;
import minipython.builder.lang.conditions.IfThenElseStatement;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.literal.BoolLiteral;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

public class RecursiveFunction {

    public static void main(String[] args) throws Exception {
        StringLiteral sRecursed = new StringLiteral("f recursed successfully");

        VariableDeclaration varR_f = new VariableDeclaration("r", Scope.SCOPE_LOCAL);
        LinkedList<Statement> bodyF = new LinkedList<>();
        FunctionDeclaration funcF = new FunctionDeclaration(
            "f",
            List.of(varR_f),
            Set.of(),
            bodyF
        );
        bodyF.add(new IfThenElseStatement(
            new ConditionalBlock(
                varR_f,
                List.of(new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(sRecursed)
                ))
            ),
            Optional.empty(),
            Optional.of(List.of(new Call(
                funcF,
                List.of(new BoolLiteral(true))
            )))
        ));

        MPyModule mod = new MPyModule(
            List.of(
                new Call(
                    funcF,
                    List.of(new BoolLiteral(false))
                )
            ),
            Set.of(),
            Set.of(),
            Set.of(funcF)
        );

        new WasmtimeCliRunner().run(minipython.builder.wasm.Transform.transform(mod).build());

        java.nio.file.Path fileOutput = java.nio.file.FileSystems.getDefault().getPath("build/compilerOutput/Regression_RecursiveFunction/");
        minipython.builder.cbuilder.Transform.transform(mod).writeProgram(fileOutput);
    }

}
