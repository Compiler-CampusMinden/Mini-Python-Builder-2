package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.BoolLiteral;
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
 * def printA():
 *   print(a)
 *
 * printA()
 * ```
 */
public class PrintBool {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        Module mod = new Module(module);
        VariableDeclaration varA = mod.newVariable("a");

        List<Statement> printABody = new LinkedList<>();
        FunctionDeclaration printA = mod.newFunction("printA", printABody);
        printABody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                varA
            })
        ));


        module.add(new VariableAssignment(varA, new BoolLiteral(true)));
        module.add(new Call(
            printA,
            List.of()
        ));

        new WasmtimeCliRunner().run(mod.build());
    }

}

