package minipython.builder.wasm.lang.literal;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_TUPLE;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_DEC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_TUPLE_ASSIGN;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;

/**
 * A MiniPython Tuple object.
 *
 * @param elements values of this tuple; in order
 */
public record TupleLiteral(
    List<Expression> elements
) implements Expression {

    public TupleLiteral(Expression... elements) {
        this(List.of(elements));
    }

	@Override
	public BlockContent buildExpression(Module partOf) {
        partOf.declareRuntimeImports(MPY_TUPLE_ASSIGN, MPY_OBJ_INIT_TUPLE);

        // the process of creating a tuple 'visualized'
        //     [BOT, elem1, elem2] i32.const 0 -> [BOT, elem1, elem2, 0]
        //     init [..., size] -> [..., tupleObj]
        //     assign [..., pos, value, tupleObj] -> [..., tupleObj]
        //
        //     1 <- pos
        //     StrObj
        //     0 <- pos
        //     IntObj
        //     2 <- size
        //
        //     V __mpy_obj_init_tuple
        //
        //     1 <- pos
        //     StrObj
        //     0 <-  pos
        //     IntObj
        //     TupleObj
        //
        //     V __mpy_tuple_assign
        //
        //     1 <- pos
        //     StrObj
        //     TupleObj
        //
        //     V __mpy_tuple_assign
        //
        //     TupleObj

        List<BlockContent> tupleInit = new ArrayList<>();

        // first: push assignment arguments of each element
        // onto the stack
        for (int i = 0; i < elements.size(); i++) {
            tupleInit.add(new Block(
                Optional.of("element at %d of tuple".formatted(i)),
                Optional.empty(),
                "",
                new Block(
                    "  ",
                    new Line("i32.const %d".formatted(i)),
                    elements.get(i).buildExpression(partOf)
                )
            ));
        }

        // then: push size onto the stack
        tupleInit.add(new Line("i32.const %d".formatted(elements.size()), "size of tuple"));

        // now: create tuple
        // and: for each element call assignment function
        tupleInit.add(new Line("call $__mpy_obj_init_tuple"));
        for (@SuppressWarnings("unused") Expression _e : elements) {
            tupleInit.add(new Line("call $__mpy_tuple_assign"));
        }

        return new Block("start of new tuple", "end of new tuple", "",
                new Block(Optional.empty(), tupleInit, Optional.empty(), "  "));
	}

	@Override
	public BlockContent buildStatement(Module partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_REF_DEC);
        return new Block(
            "",
            buildExpression(partOf),
            new Line("call $__mpy_obj_ref_dec")
        );
	}
}
