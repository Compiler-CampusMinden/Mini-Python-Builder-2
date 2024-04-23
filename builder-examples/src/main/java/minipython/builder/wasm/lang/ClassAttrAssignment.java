package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.object.AttributeAssignment;
import minipython.builder.wasm.lang.object.AttributeReference;
import minipython.builder.wasm.lang.object.MPyClass;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;
import minipython.builder.wasm.run.WasmtimeCliRunner;

/**
 * A simple demonstration of the WASM builder,
 * that creates a new class.
 *
 * Equivalent (Mini-)Python code:
 * ```python
 * class A:
 *   def __init__(self):
 *     super()
 *     print("initialising new A")
 *     self.a = 42
 *
 *  a = A()
 *  print(a.a)
 * ```
 *
 * note that super actually requires passing self in the builer
 */
public class ClassAttrAssignment {

    public static void main(String[] args) throws Exception {
        List<Statement> module = new ArrayList<>();
        Module mod = new Module(module);

        StringLiteral a = mod.newString("a");
        VariableDeclaration varA = mod.newVariable(a);

        MPyClass clazz = mod.newClass(mod.newString("A"), Builtins.TYPE_OBJECT, Map.of());

        List<Statement> initBody = new LinkedList<>();
        FunctionDeclaration init = clazz.newFunction(mod.newString("__init__"), initBody);
        VariableDeclaration self = init.addArgument(mod.newString("self"));
        initBody.add(new Call(Builtins.FUNCTION_SUPER, List.of(self)));
        initBody.add(new Call(Builtins.FUNCTION_PRINT, List.of(mod.newString("initialising new A"))));
        initBody.add(new AttributeAssignment(new AttributeReference(self, a), new IntLiteral(42)));

        module.add(new VariableAssignment(varA, new Call(clazz, List.of())));
        module.add(new Call(Builtins.FUNCTION_PRINT, List.of(new AttributeReference(varA, a))));

        new WasmtimeCliRunner().run(mod.build());
    }

}



