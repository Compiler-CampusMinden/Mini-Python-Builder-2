package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.literal.IntLiteral;
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
        List<Statement> module = new ArrayList<>();
        MPyModule mod = new MPyModule(module);
        VariableDeclaration varA = mod.newVariable(mod.newString("a"));

        module.add(new VariableAssignment(varA, new IntLiteral(0)));

        List<Statement> whileBody = new LinkedList<>();
        module.add(new WhileStatement(
            new Call(new AttributeReference(varA, mod.newString("__lt__")), List.of(new IntLiteral(10))),
            whileBody
        ));
        whileBody.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                varA
            })
        ));
        whileBody.add(new VariableAssignment(
            varA,
            new Call(
                new AttributeReference(varA, mod.newString("__add__")),
                List.of(new IntLiteral(1))
            )
        ));


        new WasmtimeCliRunner().run(mod.build());
    }

}

