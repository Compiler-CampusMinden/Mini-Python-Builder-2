package minipython.builder.wasm.lang.object;

import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_INIT_TYPE;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_REF_INC;
import static minipython.builder.wasm.lang.RuntimeImports.MPY_OBJ_SET_ATTR;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import minipython.builder.BlockContent;
import minipython.builder.wasm.Block;
import minipython.builder.wasm.Line;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.literal.StringLiteral;

public class MPyClass implements Expression {

    private final StringLiteral name;
    private final Expression parent;
    private final Map<StringLiteral, Expression> classAttributes;

    private final Set<FunctionDeclaration> functions;

    public MPyClass(StringLiteral name, Expression parent, Map<StringLiteral, Expression> classAttributes, Set<FunctionDeclaration> functions) {
        this.name = name;
        this.parent = parent;
        this.classAttributes = classAttributes;
        this.functions = functions;
    }

    @Override
    public BlockContent buildExpression(MPyModule partOf) {
        return new Line("global.get $%s".formatted(name.value()));
    }

    public BlockContent buildRawTypeDeclaration(MPyModule partOf) {
        return new Block(
            "start of class '%s'".formatted(name.value()),
            "end of class '%s'".formatted(name.value()),
            "",
            new Block(
                "  ",
                new Block(Optional.empty(), functions.stream().map(func -> func.buildRawFuncDeclaration(partOf)).collect(Collectors.toList()), Optional.empty(), "")
            )
        );
    }

    public BlockContent buildTypeObjDeclaration(MPyModule partOf) {
        return new Block(
            "",
            new Line("(global $%s (mut i32) (i32.const 0))".formatted(name.value()))
        );
    }

    public BlockContent buildInitialisation(MPyModule partOf) {
        partOf.declareRuntimeImport(MPY_OBJ_SET_ATTR, MPY_OBJ_INIT_TYPE, MPY_OBJ_REF_INC);

        List<BlockContent> attributes = classAttributes.entrySet().stream().map(attribute ->
            new Block(
                "start of attribute '%s'".formatted(attribute.getKey().value()),
                "end of attribute '%s'".formatted(attribute.getKey().value()),
                "",
                new Block(
                    "  ",
                    // new Line("global.get $%s".formatted(name.value())),
                    attribute.getKey().buildExpressionCString(partOf),
                    attribute.getValue().buildExpression(partOf),
                    new Line("call $__mpy_obj_set_attr")
                )
            )
        ).collect(Collectors.toList());
        List<BlockContent> functions = this.functions.stream().map(function ->
            new Block(
                "start of function '%s'".formatted(function.name()),
                "end of function '%s'".formatted(function.name()),
                "",
                new Block(
                    "  ",
                    // new Line("global.get $%s".formatted(name.value())),
                    function.nameLiteral().buildExpressionCString(partOf),
                    function.buildInitialisation(partOf),
                    new Line("call $__mpy_obj_set_attr")
                )
            )
        ).collect(Collectors.toList());

        return new Block(
            "start of class '%s' init".formatted(name.value()),
            "end of class '%s' init".formatted(name.value()),
            "",
            new Block(
                "  ",
                name.buildExpression(partOf),
                parent.buildExpression(partOf),
                new Line("call $__mpy_obj_init_type"),
                new Line("global.set $%s".formatted(name.value())),
                new Line("global.get $%s".formatted(name.value())),
                new Line("call $__mpy_obj_ref_inc"),
                new Line("global.get $%s".formatted(name.value())),
                new Line(""),
                new Block(Optional.empty(), attributes, Optional.empty(), ""),
                new Line(""),
                new Block(Optional.empty(), functions, Optional.empty(), ""),
                // class object returned by last mpy_obj_set_attr call
                new Line("drop")
            )
        );
    }

    @Override
    public BlockContent buildStatement(MPyModule partOf) {
        // referencing a class delcaration as a statement
        // is simply a no-op
        return new Line("", "class '%s'".formatted(name.value()));
    }
}
