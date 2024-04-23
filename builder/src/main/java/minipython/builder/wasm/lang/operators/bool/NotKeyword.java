package minipython.builder.wasm.lang.operators.bool;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_BOOLEAN_RAW;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_BOOLEAN;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;

import java.util.List;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Call;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.object.AttributeReference;

public record NotKeyword(
    Expression e
) implements Expression {

    @Override
    public BlockContent buildExpression(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_BOOLEAN_RAW, MPY_OBJ_INIT_BOOLEAN);
        return new Block(
            "start of boolean not",
            "end of boolean not",
            "",
            new Block(
                "  ",
                new Call(new AttributeReference(e, partOf.BUILTIN_STRINGS.ATTR_FUNC_BOOL), List.of()).buildExpression(partOf),
                new Line("call $__mpy_boolean_raw"),
                new Line("i32.eqz"),
                new Line("call $__mpy_obj_init_boolean")
            )
        );
    }

    @Override
    public BlockContent buildStatement(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_REF_DEC);
        return new Block(
            "",
            buildExpression(partOf),
            new Line("call $__mpy_obj_ref_dec")
        );
    }
}
