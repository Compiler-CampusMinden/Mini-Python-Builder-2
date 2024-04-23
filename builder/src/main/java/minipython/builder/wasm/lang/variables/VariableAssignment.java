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
        // for variable assignments,
        // the order matters:
        // a=a
        // leads to a being garbage collected
        // if first the left side (target) is decremented,
        // and afterwards the right side (value) is incremented
        // (which does make more sense in the general case,
        // since otherwise value would need to be evaluated twice).
        // Therefore special case somevar=somevar style assignments:
        if (this.value instanceof VariableDeclaration) {
            VariableDeclaration val = (VariableDeclaration) this.value;
            return new Block(
                "start of assignment to %s '%s'".formatted(target.kind(), target.name()),
                "end of assignment to %s '%s'".formatted(target.kind(), target.name()),
                "",
                new Line("%s.get $%s".formatted(val.kind(), val.name())),
                new Line("call $__mpy_obj_ref_inc"),
                new Line("%s.get $%s".formatted(target.kind(), target.name())),
                new Line("call $__mpy_obj_ref_dec"),
                value.buildExpression(partOf),
                new Line("%s.set $%s".formatted(target.kind(), target.name()))
            );
        } else {
            return new Block(
                "start of assignment to %s '%s'".formatted(target.kind(), target.name()),
                "end of assignment to %s '%s'".formatted(target.kind(), target.name()),
                "",
                new Line("%s.get $%s".formatted(target.kind(), target.name())),
                new Line("call $__mpy_obj_ref_dec"),
                value.buildExpression(partOf),
                new Line("%s.set $%s".formatted(target.kind(), target.name())),
                new Line("%s.get $%s".formatted(target.kind(), target.name())),
                new Line("call $__mpy_obj_ref_dec")
            );
        }
    }
}
