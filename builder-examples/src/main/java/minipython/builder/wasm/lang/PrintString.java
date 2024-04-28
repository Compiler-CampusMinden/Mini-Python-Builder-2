package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the string "Hello World".
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * print("Hello World")
 * ```
 */
public class PrintString {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        MPyModule mod = new MPyModule(module);

        module.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                mod.newString("Hello World")
            })
        ));

        new WasmtimeCliRunner().run(mod.build());
    }

}

