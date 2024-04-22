package minipython.builder.wasm.lang.builtin;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_BUILTINS_GET_TYPE_OBJECT;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;

public class TypeObject implements Expression {


    @Override
    public BlockContent buildExpression(Module partOf) {
        partOf.declareRuntimeImport(MPY_BUILTINS_GET_TYPE_OBJECT);
        return new Line("call $__mpy_builtins_get_type_object");
    }

    @Override
    public BlockContent buildStatement(Module partOf) {
        // referencing a builtin as a statement
        // is simply a no-op
        return new Line("", "builtin object type");
    }
}
