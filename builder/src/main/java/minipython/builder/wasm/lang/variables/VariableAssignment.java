package minipython.builder.wasm.lang.variables;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.Module;
import minipython.builder.wasm.lang.Statement;

/**
 * Assignment to an already declared \a VariableDeclaration.
 */
public record VariableAssignment(
    VariableDeclaration target,
    Expression value
) implements Statement {

    @Override
    public BlockContent buildStatement(Module partOf) {
        return new Block(
            "start of assignment to %s '%s'".formatted(target.kind(), target.name()),
            "end of assignment to %s '%s'".formatted(target.kind(), target.name()),
            "",
            value.buildExpression(partOf),
            new Line("%s.set $%s".formatted(target.kind(), target.name()))
        );
    }
}
