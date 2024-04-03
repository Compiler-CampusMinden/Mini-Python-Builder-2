package minipython.builder.transform;

import java.util.ArrayList;
import java.util.List;

import minipython.builder.lang.Statement;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.wasm.Transform;
import minipython.builder.wasm.run.WasmtimeCliRunner;
import minipython.builder.lang.Call;
import minipython.builder.lang.Expression;
import minipython.builder.lang.Module;

/**
 * A simple demonstration of the generic builder,
 * that simply prints the string "Hello World";
 * transforming the program to WASM code.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * print("Hello World")
 * ```
 */
public class PrintStringWasm {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        Module mod = new Module(module);

        module.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new StringLiteral("Hello World")
            })
        ));

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}


