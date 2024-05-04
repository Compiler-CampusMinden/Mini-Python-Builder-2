package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.literal.StringLiteral;
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
        StringLiteral sHelloWorld = new StringLiteral("Hello World");

        MPyModule mod = new MPyModule(
            List.of(
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        sHelloWorld
                    )
                )
            ),
            Set.of(),
            Set.of(),
            Set.of(),
            Set.of(
                sHelloWorld
            )
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}

