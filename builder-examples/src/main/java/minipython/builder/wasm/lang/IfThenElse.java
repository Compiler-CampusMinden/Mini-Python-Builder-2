package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.object.AttributeReference;
import minipython.builder.wasm.lang.operators.control_flow.IfThenElseStatement;
import minipython.builder.wasm.lang.operators.control_flow.IfThenElseStatement.ConditionalBlock;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the integer 42.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * def do(a):
 *   if a < 42:
 *     print("too small")
 *  elif a > 42:
 *      print("too big")
 *  elif a == 1337:
 *      print("special value")
 *  else:
 *      print("correct :)")
 * ```
 */
public class IfThenElse {

    public static void main(String[] args) throws Exception {
        StringLiteral sDo = new StringLiteral("do");
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sLt = new StringLiteral("__lt__");
        StringLiteral sTooSmall = new StringLiteral("too small");
        StringLiteral sEq = new StringLiteral("__eq__");
        StringLiteral sSpecialValue = new StringLiteral("special value");
        StringLiteral sGt = new StringLiteral("__gt__");
        StringLiteral sTooBig = new StringLiteral("too big");
        StringLiteral sCorrect = new StringLiteral("correct :)");

        VariableDeclaration varA_Do = new VariableDeclaration(sA, Scope.SCOPE_LOCAL);
        FunctionDeclaration fnDo = new FunctionDeclaration(
            sDo,
            List.of(varA_Do),
            Set.of(),
            List.of(
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(varA_Do)
                ),
                new IfThenElseStatement(
                    new ConditionalBlock(
                        new Call(
                            new AttributeReference(varA_Do, sLt),
                            List.of(new IntLiteral(42))
                        ),
                        List.of(new Call(
                            Builtins.FUNCTION_PRINT,
                            List.of(sTooSmall)
                        ))
                    ),
                    Optional.of(List.of(
                        new ConditionalBlock(
                            new Call(
                                new AttributeReference(varA_Do, sEq),
                                List.of(new IntLiteral(1337))
                            ),
                            List.of(new Call(
                                Builtins.FUNCTION_PRINT,
                                List.of(sSpecialValue)
                            ))
                        ),
                        new ConditionalBlock(
                            new Call(
                                new AttributeReference(varA_Do, sGt),
                                List.of(new IntLiteral(42))
                            ),
                            List.of(new Call(
                                Builtins.FUNCTION_PRINT,
                                List.of(sTooBig)
                            ))
                        )
                    )),
                    Optional.of(List.of(new Call(
                        Builtins.FUNCTION_PRINT,
                        List.of(sCorrect)
                    )))
                )
            )
        );

        MPyModule mod = new MPyModule(
            List.of(
                new Call(
                    fnDo,
                    List.of(new IntLiteral(41))
                ),
                new Call(
                    fnDo,
                    List.of(new IntLiteral(43))
                ),
                new Call(
                    fnDo,
                    List.of(new IntLiteral(1337))
                ),
                new Call(
                    fnDo,
                    List.of(new IntLiteral(42))
                )
            ),
            Set.of(),
            Set.of(),
            Set.of(fnDo),
            Set.of(
                sDo,
                sA,
                sLt,
                sTooSmall,
                sEq,
                sSpecialValue,
                sGt,
                sTooBig,
                sCorrect
            )
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}

