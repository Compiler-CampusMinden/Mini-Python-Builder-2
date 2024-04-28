package minipython.builder.wasm.lang.operators.control_flow;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_BOOLEAN_RAW;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Call;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.Statement;
import minipython.builder.wasm.lang.object.AttributeReference;

public record IfThenElseStatement(
    ConditionalBlock ifBlock,
    Optional<List<ConditionalBlock>> elifBlocks,
    Optional<List<Statement>> elseBody
) implements Statement {

    public record ConditionalBlock(
        Expression condition,
        List<Statement> body
    ) {

        public ConditionalBlock(Expression condition, Statement[] body) {
            this(condition, List.of(body));
        }
    }

    @Override
    public BlockContent buildStatement(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_BOOLEAN_RAW);

        // idea for elif:
        // nest each elif into the else block of the preceding (el)if.
        // To do that, elseBlock contains the top else block,
        // while currentElseBlock contains the lowest, to be created else block
        List<BlockContent> elseBlock = new LinkedList<>();
        List<BlockContent> currentElseBlock = elseBlock;

        if (elifBlocks.isPresent()) {
            for (ConditionalBlock elif : elifBlocks.get()) {
                List<BlockContent> newElseBlock = new LinkedList<>();

                currentElseBlock.add(new Block("",
                    new Line("(else"),
                    new Block("  ",
                        new Call(new AttributeReference(elif.condition, partOf.BUILTIN_STRINGS.ATTR_FUNC_BOOL), List.of()).buildExpression(partOf),
                        new Line("call $__mpy_boolean_raw"),
                        new Block("",
                            new Line("(if"),
                            new Block("  ",
                                new Line("(then"),
                                new Block(Optional.empty(), elif.body.stream().map(s -> s.buildStatement(partOf)).collect(Collectors.toList()), Optional.empty(), "  "),
                                new Line(")", "then"),
                                new Block(Optional.empty(), newElseBlock, Optional.empty(), "")
                            ),
                            new Line(")")
                        )
                    ),
                    new Line(")", "else")
                ));

                currentElseBlock = newElseBlock;
            }
        }

        if (elseBody.isPresent()) {
            currentElseBlock.add(new Block("",
                new Line("(else"),
                new Block("  ",
                    new Block(Optional.empty(), elseBody.get().stream().map(s -> s.buildStatement(partOf)).collect(Collectors.toList()), Optional.empty(), "  ")
                ),
                new Line(")", "else")
            ));
        }

        return new Block(
            "start of if/then/else",
            "end of if/then/else",
            "",
            new Call(new AttributeReference(ifBlock.condition, partOf.BUILTIN_STRINGS.ATTR_FUNC_BOOL), List.of()).buildExpression(partOf),
            new Line("call $__mpy_boolean_raw"),
            new Line("(if"),
            new Block("  ",
                new Line("(then"),
                new Block(Optional.empty(), ifBlock.body.stream().map(s -> s.buildStatement(partOf)).collect(Collectors.toList()), Optional.empty(), "  "),
                new Line(")", "then"),
                new Block(Optional.empty(), elseBlock, Optional.empty(), "")
            ),
            new Line(")", "if")
        );
    }

}

