package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.literal.IntLiteral;
import minipython.builder.lang.object.AttributeReference;
import minipython.builder.lang.conditions.WhileStatement;
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
 * a = 0
 *
 * while a < 10:
 *   print(a)
 *   a = a + 1
 * ```
 */
public class While {

    public static void main(String[] args) throws Exception {
        VariableDeclaration varA = new VariableDeclaration("a");

        MPyModule mod = new MPyModule(
            List.of(
                new Assignment(varA, new IntLiteral(0)),
                new WhileStatement(
                    new Call(
                        new AttributeReference(
                            varA,
                            "__lt__"
                        ),
                        List.of(new IntLiteral(10))
                    ),
                    List.of(
                        new Call(
                            Builtins.FUNCTION_PRINT,
                            List.of(
                                varA
                            )
                        ),
                        new Assignment(
                            varA,
                            new Call(
                                new AttributeReference(
                                    varA,
                                    "__add__"
                                ),
                                List.of(new IntLiteral(1))
                            )
                        )
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

