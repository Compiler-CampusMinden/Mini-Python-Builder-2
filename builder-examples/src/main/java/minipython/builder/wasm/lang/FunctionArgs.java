package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that prints the bool False, using a custom function receiving a single argument.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * a = False
 *
 * def printArg(a):
 *   print(a)
 *
 * printArg(a)
 * printArg(True)
 * ```
 */
public class FunctionArgs {

    public static void main(String[] args) throws Exception {
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sPrintA = new StringLiteral("printA");

        VariableDeclaration varA = new VariableDeclaration(sA);

        VariableDeclaration varA_printA = new VariableDeclaration(
            sA,
            Scope.SCOPE_LOCAL
        );
        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            sPrintA,
            List.of(varA_printA),
            Set.of(),
            List.of(
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
                new VariableAssignment(
                    varA,
                    new BoolLiteral(false)
                ),
                new Call(
                    fnPrintA,
                    List.of(
                        varA
                    )
                ),
                new Call(
                    fnPrintA,
                    List.of(
                        new BoolLiteral(true)
                    )
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


