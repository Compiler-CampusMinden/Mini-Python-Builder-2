package minipython.builder.transform;

import java.util.ArrayList;
import java.util.List;

import minipython.builder.lang.Statement;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.literal.IntLiteral;
import minipython.builder.wasm.Transform;
import minipython.builder.wasm.run.WasmtimeCliRunner;
import minipython.builder.lang.Call;
import minipython.builder.lang.Expression;
import minipython.builder.lang.Module;

/**
 * A simple demonstration of the generic builder,
 * that simply prints the integer 42;
 * transforming the program to WASM code.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * print(42)
 * ```
 */
public class PrintIntWasm {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        Module mod = new Module(module);

        module.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new IntLiteral(42)
            })
        ));

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}

