package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that prints the bool False, using a custom function receiving a single argument.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * a = False
 *
 * def printArg(a):
 *   print(a)
 *
 * printArg(a)
 * printArg(True)
 * ```
 */
public class FunctionArgs {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        MPyModule mod = new MPyModule(module);
        StringLiteral a = mod.newString("a");
        VariableDeclaration varA = mod.newVariable(a);

        List<Statement> printABody = new LinkedList<>();
        FunctionDeclaration printA = mod.newFunction(mod.newString("printA"), printABody);
        VariableDeclaration argA = printA.addArgument(a);
        printABody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                argA
            })
        ));

        module.add(new VariableAssignment(varA, new BoolLiteral(false)));
        module.add(new Call(
            printA,
            List.of(new Expression[] {
                varA
            })
        ));
        module.add(new Call(
            printA,
            List.of(new Expression[] {
                new BoolLiteral(true)
            })
        ));

        new WasmtimeCliRunner().run(mod.build());
    }

}


