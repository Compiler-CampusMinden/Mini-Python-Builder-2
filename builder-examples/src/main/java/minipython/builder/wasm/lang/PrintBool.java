package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the integer 42.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * print(True)
 * ```
 */
public class PrintBool {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        Module mod = new Module(module);

        module.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new BoolLiteral(true)
            })
        ));

        new WasmtimeCliRunner().run(mod.build());
    }

}

