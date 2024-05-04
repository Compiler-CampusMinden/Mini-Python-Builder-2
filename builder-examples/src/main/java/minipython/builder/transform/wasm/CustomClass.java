package minipython.builder.transform.wasm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import minipython.builder.lang.Call;
import minipython.builder.lang.MPyModule;
import minipython.builder.lang.Scope;
import minipython.builder.lang.builtins.Builtins;
import minipython.builder.lang.functions.FunctionDeclaration;
import minipython.builder.lang.literal.StringLiteral;
import minipython.builder.lang.object.MPyClass;
import minipython.builder.lang.variables.Assignment;
import minipython.builder.lang.variables.VariableDeclaration;
import minipython.builder.wasm.Transform;
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
 *
 *  a = A()
 *  print(a)
 * ```
 *
 * note that super actually requires passing self in the builer
 */
public class CustomClass {

    public static void main(String[] args) throws Exception {
        StringLiteral sInitNewA = new StringLiteral("initialising new A");

        VariableDeclaration varA = new VariableDeclaration("a");

        VariableDeclaration varSelf_A_init = new VariableDeclaration("self", Scope.SCOPE_LOCAL);
        MPyClass clsA = new MPyClass(
            "A",
            Builtins.CLASS_MPY_OBJECT,
            Set.of(
                new FunctionDeclaration(
                    "__init__",
                    List.of(varSelf_A_init),
                    Set.of(),
                    List.of(
                        new Call(
                            Builtins.FUNCTION_SUPER,
                            List.of(varSelf_A_init)
                        ),
                        new Call(
                            Builtins.FUNCTION_PRINT,
                            List.of(sInitNewA)
                        )
                    ),
                    Scope.SCOPE_LOCAL
                )
            ),
            Map.of()
        );

        MPyModule mod = new MPyModule(
            List.of(
                new Assignment(
                    varA,
                    new Call(clsA, List.of())
                ),
                new Call(
                    Builtins.FUNCTION_PRINT,
                    List.of(varA)
                )
            ),
            Set.of(varA),
            Set.of(clsA),
            Set.of()
        );

        new WasmtimeCliRunner().run(Transform.transform(mod).build());
    }

}


