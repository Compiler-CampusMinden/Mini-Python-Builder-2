package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that prints the int 42, using a custom function,
 * that has the value in a local variable.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * def printArg():
 *   a = 42
 *   print(a)
 * ```
 */
public class FunctionLocalVars {

    public static void main(String[] args) throws Exception {
        StringLiteral sPrintA = new StringLiteral("printA");
        StringLiteral sA = new StringLiteral("a");

        VariableDeclaration varA_printA = new VariableDeclaration(sA, Scope.SCOPE_LOCAL);
        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            sPrintA,
            List.of(),
            Set.of(varA_printA),
            List.of(
                new VariableAssignment(
                    varA_printA,
                    new IntLiteral(42)
                ),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        varA_printA
                    )
                )
            )
        );

        MPyModule mod = new MPyModule(
            List.of(
                new Call(
                    fnPrintA,
                    List.of()
                )
            ),
            Set.of(),
            Set.of(),
            Set.of(fnPrintA),
            Set.of(sPrintA, sA)
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}



