package minipython.builder.lang.conditions;

import java.util.List;
import java.util.Optional;

import minipython.builder.lang.Statement;

public record IfThenElseStatement(
    ConditionalBlock ifBlock,
    Optional<List<ConditionalBlock>> elifBlocks,
    Optional<List<Statement>> elseBody
) implements Statement {

    public IfThenElseStatement(ConditionalBlock ifBlock) {
        this(ifBlock, Optional.empty(), Optional.empty());
    }

    public IfThenElseStatement(ConditionalBlock ifBlock, List<ConditionalBlock> elifBlocks) {
        this(ifBlock, Optional.of(elifBlocks), Optional.empty());
    }

    public IfThenElseStatement(ConditionalBlock ifBlock, ConditionalBlock[] elifBlocks) {
        this(ifBlock, Optional.of(List.of(elifBlocks)), Optional.empty());
    }

    public IfThenElseStatement(ConditionalBlock ifBlock, List<ConditionalBlock> elifBlocks, List<Statement> elseBody) {
        this(ifBlock, Optional.of(elifBlocks), Optional.of(elseBody));
    }

    public IfThenElseStatement(ConditionalBlock ifBlock, List<ConditionalBlock> elifBlocks, Statement[] elseBody) {
        this(ifBlock, Optional.of(elifBlocks), Optional.of(List.of(elseBody)));
    }

    public IfThenElseStatement(ConditionalBlock ifBlock, List<Statement> elseBody, ConditionalBlock[] elifBlocks) {
        this(ifBlock, Optional.of(List.of(elifBlocks)), Optional.of(elseBody));
    }

}
