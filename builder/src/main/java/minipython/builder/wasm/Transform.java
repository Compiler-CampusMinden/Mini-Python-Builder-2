package minipython.builder.wasm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import minipython.builder.transform.Transformation;
import minipython.builder.transform.TransformationManager;
import minipython.builder.wasm.lang.Call;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.Scope;
import minipython.builder.wasm.lang.Statement;
import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.builtin.FunctionPrint;
import minipython.builder.wasm.lang.builtin.FunctionSuper;
import minipython.builder.wasm.lang.builtin.TypeObject;
import minipython.builder.wasm.lang.functions.FunctionDeclaration;
import minipython.builder.wasm.lang.functions.ReturnStatement;
import minipython.builder.wasm.lang.literal.BoolLiteral;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.literal.TupleLiteral;
import minipython.builder.wasm.lang.object.AttributeAssignment;
import minipython.builder.wasm.lang.object.AttributeReference;
import minipython.builder.wasm.lang.object.MPyClass;
import minipython.builder.wasm.lang.operators.bool.AndKeyword;
import minipython.builder.wasm.lang.operators.bool.NotKeyword;
import minipython.builder.wasm.lang.operators.bool.OrKeyword;
import minipython.builder.wasm.lang.operators.control_flow.IfThenElseStatement;
import minipython.builder.wasm.lang.operators.control_flow.WhileStatement;
import minipython.builder.wasm.lang.operators.control_flow.IfThenElseStatement.ConditionalBlock;
import minipython.builder.wasm.lang.variables.VariableAssignment;
import minipython.builder.wasm.lang.variables.VariableDeclaration;

/**
 * Transformation of MiniPython programs represented by the generic builder
 * to the representation of the WASM builder.
 */
public class Transform {

    private Set<StringLiteral> strings = new HashSet<>();
    // set to the first argument of the current function
    // when the function is local and named __init__,
    // to allow for transformation of the super call.
    private VariableDeclaration funcInitArgSelf = null;

    private class FunctionPrintTransform implements Transformation<minipython.builder.lang.builtins.FunctionPrint, FunctionPrint, Transform> {

        @Override
        public FunctionPrint apply(minipython.builder.lang.builtins.FunctionPrint from, Transform context,
                TransformationManager manager) {
            return Builtins.FUNCTION_PRINT;
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class FunctionSuperTransform implements Transformation<minipython.builder.lang.builtins.FunctionSuper, FunctionSuper, Transform> {

        @Override
        public FunctionSuper apply(minipython.builder.lang.builtins.FunctionSuper from, Transform context, TransformationManager manager) {
            return Builtins.FUNCTION_SUPER;
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class TypeObjectTransform implements Transformation<minipython.builder.lang.builtins.ClassMPyObject, TypeObject, Transform> {

        @Override
        public TypeObject apply(minipython.builder.lang.builtins.ClassMPyObject from, Transform context, TransformationManager manager) {
            return Builtins.TYPE_OBJECT;
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class IfThenElseTransform implements Transformation<minipython.builder.lang.conditions.IfThenElseStatement, IfThenElseStatement, Transform> {

        @Override
        public IfThenElseStatement apply(minipython.builder.lang.conditions.IfThenElseStatement from, Transform context, TransformationManager manager) {
            return new IfThenElseStatement(
                new ConditionalBlock(
                    manager.transform(from.ifBlock().condition(), context, Expression.class),
                    from.ifBlock().body().stream().map(
                        s -> manager.transform(s, context, Statement.class)
                    ).toList()
                ),
                from.elifBlocks().map(elifBlocks ->
                    elifBlocks.stream().map(
                        b -> new ConditionalBlock(
                            manager.transform(b.condition(), context, Expression.class),
                            b.body().stream().map(
                                s -> manager.transform(s, context, Statement.class)
                            ).toList()
                        )
                    ).toList()
                ),
                from.elseBody().map(body ->
                    body.stream().map(s ->
                        manager.transform(s, context, Statement.class)
                    ).toList()
                )
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class WhileStatementTransform implements Transformation<minipython.builder.lang.conditions.WhileStatement, WhileStatement, Transform> {

        @Override
        public WhileStatement apply(minipython.builder.lang.conditions.WhileStatement from, Transform context,
                TransformationManager manager) {
            return new WhileStatement(
                manager.transform(
                    from.statement().condition(),
                    context,
                    Expression.class
                ),
                from.statement().body().stream().map(s ->
                    manager.transform(s, context, Statement.class)
                ).toList()
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class FunctionDeclarationTransform implements Transformation<minipython.builder.lang.functions.FunctionDeclaration, FunctionDeclaration, Transform> {

        @Override
        public FunctionDeclaration apply(minipython.builder.lang.functions.FunctionDeclaration from, Transform context,
                TransformationManager manager) {

            Scope scope = manager.transform(from.scope(), context, Scope.class);
            boolean isFuncInit = scope == Scope.SCOPE_GLOBAL && from.name().equals("__init__");
            VariableDeclaration prevFuncInitArgSelf = context.funcInitArgSelf;
            // allow SuperCallTransform to access the self argument
            if (isFuncInit) {
                context.funcInitArgSelf = manager.transform(from.arguments().get(0), context, VariableDeclaration.class);
            }

            FunctionDeclaration fn = new FunctionDeclaration(
                manager.transform(from.name(), context, StringLiteral.class),
                from.arguments().stream().map(a ->
                    manager.transform(a, context, VariableDeclaration.class)
                ).toList(),
                from.localVariables().stream().map(l ->
                    manager.transform(l, context, VariableDeclaration.class)
                ).collect(Collectors.toSet()),
                from.body().stream().map(s ->
                    manager.transform(s, context, Statement.class)
                ).toList(),
                scope
            );

            context.funcInitArgSelf = prevFuncInitArgSelf;

            return fn;
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class ReturnStatementTransform implements Transformation<minipython.builder.lang.functions.ReturnStatement, ReturnStatement, Transform> {

        @Override
        public ReturnStatement apply(minipython.builder.lang.functions.ReturnStatement from, Transform context,
                TransformationManager manager) {
            return new ReturnStatement(manager.transform(from.value(), context, Expression.class));
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class AndKeywordTransform implements Transformation<minipython.builder.lang.keyword.AndKeyword, AndKeyword, Transform> {

        @Override
        public AndKeyword apply(minipython.builder.lang.keyword.AndKeyword from, Transform context, TransformationManager manager) {
            return new AndKeyword(
                manager.transform(from.left(), context, Expression.class),
                manager.transform(from.right(), context, Expression.class)
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class NotKeywordTransform implements Transformation<minipython.builder.lang.keyword.NotKeyword, NotKeyword, Transform> {

        @Override
        public NotKeyword apply(minipython.builder.lang.keyword.NotKeyword from, Transform context, TransformationManager manager) {
            return new NotKeyword(manager.transform(from.e(), context, Expression.class));
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class OrKeywordTransform implements Transformation<minipython.builder.lang.keyword.OrKeyword, OrKeyword, Transform> {

        @Override
        public OrKeyword apply(minipython.builder.lang.keyword.OrKeyword from, Transform context, TransformationManager manager) {
            return new OrKeyword(
                manager.transform(from.left(), context, Expression.class),
                manager.transform(from.right(), context, Expression.class)
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class BoolLiteralTransform implements Transformation<minipython.builder.lang.literal.BoolLiteral, BoolLiteral, Transform> {

        @Override
        public BoolLiteral apply(minipython.builder.lang.literal.BoolLiteral from, Transform context,
                TransformationManager manager) {
            return new BoolLiteral(from.value());
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class IntLiteralTransform implements Transformation<minipython.builder.lang.literal.IntLiteral, IntLiteral, Transform> {

        @Override
        public IntLiteral apply(minipython.builder.lang.literal.IntLiteral from, Transform context,
                TransformationManager manager) {
            return new IntLiteral(from.value());
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class StringLiteralTransform implements Transformation<minipython.builder.lang.literal.StringLiteral, StringLiteral, Transform> {

        @Override
        public StringLiteral apply(minipython.builder.lang.literal.StringLiteral from, Transform context, TransformationManager manager) {
            StringLiteral lit = new StringLiteral(from.value());
            context.strings.add(lit);
            return lit;
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class StringToStringLiteralTransform implements Transformation<String, StringLiteral, Transform> {

        @Override
        public StringLiteral apply(String from, Transform context, TransformationManager manager) {
            StringLiteral lit = new StringLiteral(from);
            context.strings.add(lit);
            return lit;
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class TupleLiteralTransform implements Transformation<minipython.builder.lang.literal.TupleLiteral, TupleLiteral, Transform> {

        @Override
        public TupleLiteral apply(minipython.builder.lang.literal.TupleLiteral from, Transform context,
                TransformationManager manager) {
            return new TupleLiteral(from.elements().stream().map(e -> manager.transform(e, context, Expression.class)).collect(Collectors.toList()));
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class AttributeAssignmentTransform implements Transformation<minipython.builder.lang.object.AttributeAssignment, AttributeAssignment, Transform> {

        @Override
        public AttributeAssignment apply(minipython.builder.lang.object.AttributeAssignment from, Transform context,
                TransformationManager manager) {
            return new AttributeAssignment(
                manager.transform(
                    from.attribute(),
                    context,
                    AttributeReference.class
                ),
                manager.transform(
                    from.value(),
                    context,
                    Expression.class
                )
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class AttributeReferenceTransform implements Transformation<minipython.builder.lang.object.AttributeReference, AttributeReference, Transform> {

        @Override
        public AttributeReference apply(minipython.builder.lang.object.AttributeReference from, Transform context,
                TransformationManager manager) {
            return new AttributeReference(
                manager.transform(
                    from.object(),
                    context,
                    Expression.class
                ),
                manager.transform(
                    from.attributeName(),
                    context,
                    StringLiteral.class
                )
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class MPyClassTransform implements Transformation<minipython.builder.lang.object.MPyClass, MPyClass, Transform> {

        @Override
        public MPyClass apply(minipython.builder.lang.object.MPyClass from, Transform context,
                TransformationManager manager) {
            return new MPyClass(
                manager.transform(from.name(), context, StringLiteral.class),
                manager.transform(from.parent(), context, Expression.class),
                from.classAttributes().entrySet().stream().collect(Collectors.toMap(
                    entry -> manager.transform(entry.getKey(), context, StringLiteral.class),
                    entry -> manager.transform(entry.getValue(), context, Expression.class)
                )),
                from.functions().stream().map(f ->
                    manager.transform(f, context, FunctionDeclaration.class)
                ).collect(Collectors.toSet())
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class SuperCallTransform implements Transformation<minipython.builder.lang.object.SuperCall, Call, Transform> {

        @Override
        public Call apply(minipython.builder.lang.object.SuperCall from, Transform context, TransformationManager manager) {

            List<Expression> positionalArgs = from.positionalArgs().stream().map(e ->
                manager.transform(e, context, Expression.class)
            ).toList();

            assert(context.funcInitArgSelf != null);
            positionalArgs.add(0, context.funcInitArgSelf);

            return new Call(
                Builtins.FUNCTION_SUPER,
                positionalArgs
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class VariableAssignmentTransform implements Transformation<minipython.builder.lang.variables.Assignment, VariableAssignment, Transform> {

        @Override
        public VariableAssignment apply(minipython.builder.lang.variables.Assignment from, Transform context, TransformationManager manager) {
            return new VariableAssignment(
                manager.transform(from.lhs(), context, VariableDeclaration.class),
                manager.transform(from.rhs(), context, Expression.class)
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class VariableDeclarationTransform implements Transformation<minipython.builder.lang.variables.VariableDeclaration, VariableDeclaration, Transform> {

        @Override
        public VariableDeclaration apply(minipython.builder.lang.variables.VariableDeclaration from, Transform context, TransformationManager manager) {
            return new VariableDeclaration(
                manager.transform(from.name(), context, StringLiteral.class),
                manager.transform(from.scope(), context, Scope.class)
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class CallTransform implements Transformation<minipython.builder.lang.Call, Call, Transform> {

        @Override
        public Call apply(minipython.builder.lang.Call from, Transform context,
                TransformationManager manager) {
            return new Call(manager.transform(from.callable(), context, Expression.class), from.positionalArgs().stream().map(e -> manager.transform(e, context, Expression.class)).collect(Collectors.toList()));
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class ModuleTransform implements Transformation<minipython.builder.lang.MPyModule, MPyModule, Transform> {

        @Override
        public MPyModule apply(minipython.builder.lang.MPyModule from, Transform context,
                TransformationManager manager) {
            return new MPyModule(
                from.body().stream().map(
                    s -> manager.transform(s, context, Statement.class)
                ).toList(),
                from.globalVariabes().stream().map(
                    v -> manager.transform(v, context, VariableDeclaration.class)
                ).collect(Collectors.toSet()),
                from.classes().stream().map(
                    c -> manager.transform(c, context, MPyClass.class)
                ).collect(Collectors.toSet()),
                from.functions().stream().map(
                    f -> manager.transform(f, context, FunctionDeclaration.class)
                ).collect(Collectors.toSet()),
                context.strings
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class ScopeTransform implements Transformation<minipython.builder.lang.Scope, Scope, Transform> {

        @Override
        public Scope apply(minipython.builder.lang.Scope from, Transform context, TransformationManager manager) {
            switch (from) {
                case SCOPE_GLOBAL:
                    return Scope.SCOPE_GLOBAL;
                case SCOPE_LOCAL:
                    return Scope.SCOPE_LOCAL;
                default:
                    throw new RuntimeException("Unexpected enum variant: " + from);
            }
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    /**
     * Transform a MiniPython program tree to a WASM/c-runtime based tree.
     *
     * Modifications to \a module after calling this method are *not*
     * visible in the already returned WASM module.
     *
     * The current transformation architecture does not support incremental transformation.
     */
    public static MPyModule transform(minipython.builder.lang.MPyModule module) {
        return new Transform().transform_(module);
    }

    // this cannot be part of the static method,
    // since the inner classes can only be instantiated
    // if they have an eclosing instance of Transform
    public MPyModule transform_(minipython.builder.lang.MPyModule module) {
        TransformationManager manager = new TransformationManager();

        manager.registerTransformation(new FunctionPrintTransform(), minipython.builder.lang.builtins.FunctionPrint.class);
        manager.registerTransformation(new FunctionSuperTransform(), minipython.builder.lang.builtins.FunctionSuper.class);
        manager.registerTransformation(new TypeObjectTransform(), minipython.builder.lang.builtins.ClassMPyObject.class);
        manager.registerTransformation(new IfThenElseTransform(), minipython.builder.lang.conditions.IfThenElseStatement.class);
        manager.registerTransformation(new WhileStatementTransform(), minipython.builder.lang.conditions.WhileStatement.class);
        manager.registerTransformation(new FunctionDeclarationTransform(), minipython.builder.lang.functions.FunctionDeclaration.class);
        manager.registerTransformation(new ReturnStatementTransform(), minipython.builder.lang.functions.ReturnStatement.class);
        manager.registerTransformation(new AndKeywordTransform(), minipython.builder.lang.keyword.AndKeyword.class);
        manager.registerTransformation(new NotKeywordTransform(), minipython.builder.lang.keyword.NotKeyword.class);
        manager.registerTransformation(new OrKeywordTransform(), minipython.builder.lang.keyword.OrKeyword.class);
        manager.registerTransformation(new BoolLiteralTransform(), minipython.builder.lang.literal.BoolLiteral.class);
        manager.registerTransformation(new IntLiteralTransform(), minipython.builder.lang.literal.IntLiteral.class);
        manager.registerTransformation(new StringLiteralTransform(), minipython.builder.lang.literal.StringLiteral.class);
        manager.registerTransformation(new StringToStringLiteralTransform(), String.class);
        manager.registerTransformation(new TupleLiteralTransform(), minipython.builder.lang.literal.TupleLiteral.class);
        manager.registerTransformation(new AttributeAssignmentTransform(), minipython.builder.lang.object.AttributeAssignment.class);
        manager.registerTransformation(new AttributeReferenceTransform(), minipython.builder.lang.object.AttributeReference.class);
        manager.registerTransformation(new MPyClassTransform(), minipython.builder.lang.object.MPyClass.class);
        manager.registerTransformation(new SuperCallTransform(), minipython.builder.lang.object.SuperCall.class);
        manager.registerTransformation(new VariableAssignmentTransform(), minipython.builder.lang.variables.Assignment.class);
        manager.registerTransformation(new VariableDeclarationTransform(), minipython.builder.lang.variables.VariableDeclaration.class);
        manager.registerTransformation(new CallTransform(), minipython.builder.lang.Call.class);
        manager.registerTransformation(new ModuleTransform(), minipython.builder.lang.MPyModule.class);
        manager.registerTransformation(new ScopeTransform(), minipython.builder.lang.Scope.class);

        return manager.transform(module, this, MPyModule.class);
    }
}
