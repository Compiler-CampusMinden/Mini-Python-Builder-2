package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
        StringLiteral sA = new StringLiteral("a");
        StringLiteral sCapitalA = new StringLiteral("A");
        StringLiteral sSelf = new StringLiteral("self");
        StringLiteral sInit = new StringLiteral("__init__");
        StringLiteral sInitNewA = new StringLiteral("initialising new A");

        VariableDeclaration varA = new VariableDeclaration(sA);

        VariableDeclaration varSelf_A_init = new VariableDeclaration(sSelf, Scope.SCOPE_LOCAL);
        MPyClass clsA = new MPyClass(
            sCapitalA,
            Builtins.TYPE_OBJECT,
            Map.of(),
            Set.of(
                new FunctionDeclaration(
                    sInit,
                    List.of(
                        varSelf_A_init
                    ),
                    Set.of(),
                    List.of(
                        new Call(
                            Builtins.FUNCTION_SUPER,
                            List.of(varSelf_A_init)
                        ),
                        new Call(
                            Builtins.FUNCTION_PRINT,
                            List.of(
                                sInitNewA
                            )
                        ),
                        new AttributeAssignment(
                            new AttributeReference(varSelf_A_init, sA),
                            new IntLiteral(42)
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
                    List.of(
                        new AttributeReference(varA, sA)
                    )
                )
            ),
            Set.of(varA),
            Set.of(clsA),
            Set.of(),
            Set.of(
                sA,
                sCapitalA,
                sSelf,
                sInit,
                sInitNewA
            )
        );

        new WasmtimeCliRunner().run(mod.build());
    }

}



