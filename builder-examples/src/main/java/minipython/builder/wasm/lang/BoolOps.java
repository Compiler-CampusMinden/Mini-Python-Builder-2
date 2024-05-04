package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.functions.ReturnStatement;
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.operators.bool.AndKeyword;
import minipython.builder.wasm.lang.operators.bool.NotKeyword;
import minipython.builder.wasm.lang.operators.bool.OrKeyword;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.lang.variables.VariableAssignment;
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
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sTruth = new StringLiteral("truth");
        StringLiteral sTruthCalled = new StringLiteral("truth() called");
        StringLiteral sPrintA = new StringLiteral("printA");

        VariableDeclaration varA = new VariableDeclaration(sA);

        FunctionDeclaration fnTruth = new FunctionDeclaration(
            sTruth,
            List.of(
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(sTruthCalled)
                ),
                new ReturnStatement(new BoolLiteral(true))
            )
        );

        FunctionDeclaration fnPrintA = new FunctionDeclaration(
            sPrintA,
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
                new VariableAssignment(varA, new BoolLiteral(true)),
                new Call(
                    fnPrintA,
                    List.of()
                )
            ),
            Set.of(varA),
            Set.of(),
            Set.of(fnPrintA, fnTruth),
            Set.of(
                sA,
                sTruth,
                sTruthCalled,
                sPrintA
            )
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}

