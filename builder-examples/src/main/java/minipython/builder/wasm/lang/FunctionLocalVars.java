package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that prints the int 42, using a custom function,
 * that has the value in a local variable.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * def printArg():
 *   a = 42
 *   print(a)
 * ```
 */
public class FunctionLocalVars {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        MPyModule mod = new MPyModule(module);

        List<Statement> printABody = new LinkedList<>();
        FunctionDeclaration printA = mod.newFunction(mod.newString("printA"), printABody);
        VariableDeclaration varA = printA.addLocalVariable(mod.newString("a"));

        printABody.add(new VariableAssignment(varA, new IntLiteral(42)));
        printABody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                varA
            })
        ));

        module.add(new Call(
            printA,
            List.of()
        ));

        new WasmtimeCliRunner().run(mod.build());
    }

}



