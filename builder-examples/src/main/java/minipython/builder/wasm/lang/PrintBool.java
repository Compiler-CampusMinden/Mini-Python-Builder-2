package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the integer 42.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * a = True
 *
 * def printA():
 *   print(a)
 *
 * printA()
 * ```
 */
public class PrintBool {

    public static void main(String[] args) throws Exception {
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sPrintA = new StringLiteral("printA");

        VariableDeclaration varA = new VariableDeclaration(sA);

        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            sPrintA,
            List.of(
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        varA
                    )
                )
            )
        );

        MPyModule mod = new MPyModule(
            List.of(
                new VariableAssignment(
                    varA,
                    new BoolLiteral(true)
                ),
                new Call(
                    fnPrintA,
                    List.of()
                )
            ),
            Set.of(varA),
            Set.of(),
            Set.of(fnPrintA),
            Set.of(
                sA,
                sPrintA
            )
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}

