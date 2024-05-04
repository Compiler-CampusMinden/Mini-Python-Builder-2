package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.object.AttributeReference;
import minipython.builder.wasm.lang.operators.control_flow.WhileStatement;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
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
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sAdd = new StringLiteral("__add__");
        StringLiteral sLt = new StringLiteral("__lt__");

        VariableDeclaration varA = new VariableDeclaration(sA);

        MPyModule mod = new MPyModule(
            List.of(
                new VariableAssignment(varA, new IntLiteral(0)),
                new WhileStatement(
                    new Call(
                        new AttributeReference(
                            varA,
                            sLt
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
                        new VariableAssignment(
                            varA,
                            new Call(
                                new AttributeReference(
                                    varA,
                                    sAdd
                                ),
                                List.of(new IntLiteral(1))
                            )
                        )
                    )
                )
            ),
            Set.of(varA),
            Set.of(),
            Set.of(),
            Set.of(sA, sAdd, sLt)
        );


        new WasmtimeCliRunner().run(mod.build());
    }

}

