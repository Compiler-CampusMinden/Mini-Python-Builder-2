package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.literal.BoolLiteral;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.wasm.Transform;
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
        VariableDeclaration varA = new VariableDeclaration("a");

        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            "printA",
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
                new Assignment(
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
            Set.of(fnPrintA)
        );

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}

