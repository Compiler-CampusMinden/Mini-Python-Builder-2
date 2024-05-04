package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.object.AttributeReference;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
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
public class AddInt {

    public static void main(String[] args) throws Exception {
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sAdd = new StringLiteral("__add__");

        VariableDeclaration varA = new VariableDeclaration(sA);

        MPyModule mod = new MPyModule(
            List.of(
                new VariableAssignment(varA, new IntLiteral(21)),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        new Call(
                            new AttributeReference(varA, sAdd),
                            List.of(
                                varA
                            )
                        )
                    )
                )
            ),
            Set.of(varA),
            Set.of(),
            Set.of(),
            Set.of(sA, sAdd)
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}

