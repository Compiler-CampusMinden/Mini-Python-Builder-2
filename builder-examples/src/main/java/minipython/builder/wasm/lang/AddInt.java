package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.List;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.object.AttributeReference;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that simply prints the integer 42.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * a = 42
 * print(a)
 * ```
 */
public class AddInt {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        Module mod = new Module(module);
        VariableDeclaration varA = mod.newVariable(mod.newString("a"));
        StringLiteral add = mod.newString("__add__");

        module.add(new VariableAssignment(varA, new IntLiteral(21)));
        module.add(new Call(
            Builtins.FUNCTION_PRINT,
            List.of(new Expression[] {
                new Call(
                    new AttributeReference(varA, add),
                    List.of(new Expression[] {
                        varA
                    })
                )
            })
        ));

        new WasmtimeCliRunner().run(mod.build());
    }

}

