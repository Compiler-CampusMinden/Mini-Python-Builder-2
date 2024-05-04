package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Map;
import java.util.Set;

import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.StringLiteral;
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
 *
 *  a = A()
 *  print(a)
 * ```
 *
 * note that super actually requires passing self in the builer
 */
public class CustomClass {

    public static void main(String[] args) throws Exception {
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sCapitalA = new StringLiteral("A");
        StringLiteral sInitNewA = new StringLiteral("initialising new A");
        StringLiteral sInit = new StringLiteral("__init__");
        StringLiteral sSelf = new StringLiteral("self");

        VariableDeclaration varA = new VariableDeclaration(sA);

        VariableDeclaration varSelf_A_init = new VariableDeclaration(sA, Scope.SCOPE_LOCAL);
        MPyClass clsA = new MPyClass(
            sCapitalA,
            Builtins.TYPE_OBJECT,
            Map.of(),
            Set.of(
                new FunctionDeclaration(
                    sInit,
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
            )
        );

        MPyModule mod = new MPyModule(
            List.of(
                new VariableAssignment(
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
            Set.of(),
            Set.of(
                sA,
                sCapitalA,
                sInitNewA,
                sInit,
                sSelf
            )
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}


