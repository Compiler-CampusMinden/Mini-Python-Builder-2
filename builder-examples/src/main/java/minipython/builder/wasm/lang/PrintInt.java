package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the integer 42.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * print(42)
 * ```
 */
public class PrintInt {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        Module mod = new Module(module);

        module.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new IntLiteral(42)
            })
        ));

        new WasmtimeCliRunner().run(mod.build());
    }

}
