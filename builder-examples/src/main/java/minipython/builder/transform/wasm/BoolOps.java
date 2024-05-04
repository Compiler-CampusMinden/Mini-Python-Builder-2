package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.functions.ReturnStatement;
import minipython.builder.lang.literal.BoolLiteral;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.lang.keyword.AndKeyword;
import minipython.builder.lang.keyword.NotKeyword;
import minipython.builder.lang.keyword.OrKeyword;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.wasm.Transform;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the integer 42.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * a = True
 *
 * def truth():
 *   return True
 *
 * def printA():
 *   print(!a)
 *   print(a && a)
 *
 * printA()
 * ```
 */
public class BoolOps {

    public static void main(String[] args) throws Exception {
        StringLiteral sTruthCalled = new StringLiteral("truth() called");

        VariableDeclaration varA = new VariableDeclaration("a");

        FunctionDeclaration fnTruth = new FunctionDeclaration(
            "truth",
            List.of(
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(sTruthCalled)
                ),
                new ReturnStatement(new BoolLiteral(true))
            )
        );

        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            "printA",
            List.of(
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        new NotKeyword(varA)
                    )
                ),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        new AndKeyword(
                            new BoolLiteral(false),
                            new Call(fnTruth, List.of())
                        )
                    )
                ),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        new AndKeyword(
                            new BoolLiteral(true),
                            new Call(fnTruth, List.of())
                        )
                    )
                ),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        new OrKeyword(
                            new BoolLiteral(true),
                            new Call(fnTruth, List.of())
                        )
                    )
                ),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(
                        new OrKeyword(
                            new BoolLiteral(false),
                            new Call(fnTruth, List.of())
                        )
                    )
                )
            )
        );

        MPyModule mod = new MPyModule(
            List.of(
                new Assignment(varA, new BoolLiteral(true)),
                new Call(
                    fnPrintA,
                    List.of()
                )
            ),
            Set.of(varA),
            Set.of(),
            Set.of(fnPrintA, fnTruth)
        );

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}

