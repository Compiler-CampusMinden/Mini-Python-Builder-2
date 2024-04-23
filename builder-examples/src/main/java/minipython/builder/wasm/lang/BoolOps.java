package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.functions.ReturnStatement;
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.operators.bool.AndKeyword;
import minipython.builder.wasm.lang.operators.bool.NotKeyword;
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
        List<Statement> module = new ArrayList<>();
        MPyModule mod = new MPyModule(module);
        VariableDeclaration varA = mod.newVariable(mod.newString("a"));

        StringLiteral truth = mod.newString("truth");
        List<Statement> truthBody = new LinkedList<>();
        FunctionDeclaration truthFn = mod.newFunction(truth, truthBody);
        truthBody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                mod.newString("truth() called")
            })
        ));
        truthBody.add(new ReturnStatement(new BoolLiteral(true)));

        List<Statement> printABody = new LinkedList<>();
        FunctionDeclaration printA = mod.newFunction(mod.newString("printA"), printABody);
        printABody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new NotKeyword(varA)
            })
        ));
        printABody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new AndKeyword(new BoolLiteral(false), new Call(truthFn, List.of()))
            })
        ));
        printABody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new AndKeyword(new BoolLiteral(true), new Call(truthFn, List.of()))
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

