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
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.lang.object.AttributeReference;

public record OrKeyword(
    Expression left,
    Expression right
) implements Expression {

    @Override
    public BlockContent buildExpression(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_BOOLEAN_RAW);
        // idea:
        // 1. left side to raw boolean
        // 2. if true: short circuit to true
        // 3. else: evaluate right side
        //  (left side is already false, so either (false || false = false) -> false = flase or (false || true = true) -> true = true)
        //4. jump to end of block
        return new Block(
            "start of boolean and",
            "end of boolean and",
            "",
            new Block(
                "  ",
                new Line("(block $skipShortcircuit (result i32)"),
                new Block("  ",
                    new Line("(block $shortcircuit"),
                    new Block("  ",
                        new Call(new AttributeReference(left, partOf.BUILTIN_STRINGS.ATTR_FUNC_BOOL), List.of()).buildExpression(partOf),
                        new Line("call $__mpy_boolean_raw"),
                        new Line("br_if $shortcircuit"),
                        new Call(new AttributeReference(right, partOf.BUILTIN_STRINGS.ATTR_FUNC_BOOL), List.of()).buildExpression(partOf),
                        new Line("br $skipShortcircuit")
                    ),
                    new Line(")"),
                    new BoolLiteral(true).buildExpression(partOf)
                ),
                new Line(")")
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

