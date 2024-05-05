package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.Scope;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.literal.IntLiteral;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.wasm.Transform;
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
        VariableDeclaration varA_printA = new VariableDeclaration("a", Scope.SCOPE_LOCAL);
        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            "printA",
            List.of(),
            Set.of(varA_printA),
            List.of(
                new Assignment(
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
            Set.of(fnPrintA)
        );

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}



