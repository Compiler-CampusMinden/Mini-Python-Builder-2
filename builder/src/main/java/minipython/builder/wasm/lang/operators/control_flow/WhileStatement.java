package minipython.builder.wasm.lang.operators.control_flow;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_BOOLEAN_RAW;

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

public record WhileStatement(
    Expression condition,
    List<Statement> body
) implements Statement {

    @Override
    public BlockContent buildStatement(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_BOOLEAN_RAW);
        return new Block(
            "start of while",
            "end of while",
            "",
            new Line("(block $whileEnd"),
            new Block("  ",
                new Line("(loop $while"),
                new Block("  ",
                    new Call(new AttributeReference(condition, partOf.BUILTIN_STRINGS.ATTR_FUNC_BOOL), List.of()).buildExpression(partOf),
                    new Line("call $__mpy_boolean_raw"),
                    new Line("i32.eqz"),
                    new Line("br_if $whileEnd"),
                    new Line(""),
                    new Block(Optional.empty(), body.stream().map(s -> s.buildStatement(partOf)).collect(Collectors.toList()), Optional.empty(), ""),
                    new Line(""),
                    new Line("br $while")
                ),
                new Line(")", "loop")
            ),
           new Line(")", "whileEnd")
        );
    }
}
