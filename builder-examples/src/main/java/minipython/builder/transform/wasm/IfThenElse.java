package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.Scope;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.literal.IntLiteral;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.lang.object.AttributeReference;
import minipython.builder.lang.conditions.IfThenElseStatement;
import minipython.builder.lang.conditions.ConditionalBlock;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.wasm.Transform;
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
        StringLiteral sTooSmall = new StringLiteral("too small");
        StringLiteral sSpecialValue = new StringLiteral("special value");
        StringLiteral sTooBig = new StringLiteral("too big");
        StringLiteral sCorrect = new StringLiteral("correct :)");

        VariableDeclaration varA_Do = new VariableDeclaration("a", Scope.SCOPE_LOCAL);
        FunctionDeclaration fnDo = new FunctionDeclaration(
            "do",
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
                            new AttributeReference(varA_Do, "__lt__"),
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
                                new AttributeReference(varA_Do, "__eq__"),
                                List.of(new IntLiteral(1337))
                            ),
                            List.of(new Call(
                                Builtins.FUNCTION_PRINT,
                                List.of(sSpecialValue)
                            ))
                        ),
                        new ConditionalBlock(
                            new Call(
                                new AttributeReference(varA_Do, "__gt__"),
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
            Set.of(fnDo)
        );

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}

