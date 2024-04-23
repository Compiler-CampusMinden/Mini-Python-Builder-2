package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.object.AttributeReference;
import minipython.builder.wasm.lang.operators.control_flow.IfThenElseStatement;
import minipython.builder.wasm.lang.operators.control_flow.IfThenElseStatement.ConditionalBlock;
import minipython.builder.wasm.lang.variables.VariableAssignment;
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
        List<Statement> module = new ArrayList<>();
        MPyModule mod = new MPyModule(module);

        List<Statement> doBody = new LinkedList<>();

        FunctionDeclaration doFn = mod.newFunction(mod.newString("do"), doBody);
        VariableDeclaration varA = doFn.addArgument(mod.newString("a"));
        doBody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(varA)
        ));
        doBody.add(new IfThenElseStatement(
            new ConditionalBlock(
                new Call(
                    new AttributeReference(varA, mod.newString("__lt__")),
                    List.of(new IntLiteral(42))
                ),
                List.of(new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(mod.newString("too small"))
                ))
            ),
            Optional.of(List.of(
                new ConditionalBlock(
                    new Call(
                        new AttributeReference(varA, mod.newString("__eq__")),
                        List.of(new IntLiteral(1337))
                    ),
                    List.of(new Call(
                        Builtins.FUNCTION_PRINT,
                        List.of(mod.newString("special value"))
                    ))
                ),
                new ConditionalBlock(
                    new Call(
                        new AttributeReference(varA, mod.newString("__gt__")),
                        List.of(new IntLiteral(42))
                    ),
                    List.of(new Call(
                        Builtins.FUNCTION_PRINT,
                        List.of(mod.newString("too big"))
                    ))
                )
            )),
            Optional.of(List.of(new Call(
                Builtins.FUNCTION_PRINT,
                List.of(mod.newString("correct :)"))
            )))
        ));

        module.add(new Call(
            doFn,
            List.of(new IntLiteral(41))
        ));
        module.add(new Call(
            doFn,
            List.of(new IntLiteral(43))
        ));
        module.add(new Call(
            doFn,
            List.of(new IntLiteral(1337))
        ));
        module.add(new Call(
            doFn,
            List.of(new IntLiteral(42))
        ));
        new WasmtimeCliRunner().run(mod.build());
    }

}

