package minipython.builder.wasm.lang.builtin;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_BUILTINS_GET_TYPE_FUNCTION;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;

public class TypeFunction implements Expression {


    @Override
    public BlockContent buildExpression(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_BUILTINS_GET_TYPE_FUNCTION);
        return new Line("call $__mpy_builtins_get_type_function");
    }

    @Override
    public BlockContent buildStatement(MPyModule partOf) {
        // referencing a builtin as a statement
        // is simply a no-op
        return new Line("", "builtin function type");
    }
}

