package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.Scope;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.literal.BoolLiteral;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.wasm.Transform;
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
        VariableDeclaration varA = new VariableDeclaration("a");

        VariableDeclaration varA_printA = new VariableDeclaration(
            "a",
            Scope.SCOPE_LOCAL
        );
        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            "printA",
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
                new Assignment(
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
            Set.of(fnPrintA)
        );

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}


