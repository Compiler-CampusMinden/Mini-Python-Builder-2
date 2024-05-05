package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.literal.IntLiteral;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.wasm.Transform;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the integer 42.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * a = 42
 * print(a)
 * ```
 */
public class PrintInt {

    public static void main(String[] args) throws Exception {
        VariableDeclaration varA = new VariableDeclaration("a");

        MPyModule mod = new MPyModule(
            List.of(
                new Assignment(
                    varA,
                    new IntLiteral(42)
                ),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        varA
                    )
                )
            ),
            Set.of(varA),
            Set.of(),
            Set.of()
        );

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}
