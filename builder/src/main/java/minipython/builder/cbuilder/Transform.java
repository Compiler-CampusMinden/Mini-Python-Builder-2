package minipython.builder.cbuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.ProgramBuilder;
import minipython.builder.cbuilder.lang.Reference;
import minipython.builder.cbuilder.lang.Statement;
import minipython.builder.cbuilder.lang.conditions.IfThenElseStatement;
import minipython.builder.cbuilder.lang.conditions.conditionalStatement.ElifStatement;
import minipython.builder.cbuilder.lang.conditions.conditionalStatement.ElseStatement;
import minipython.builder.cbuilder.lang.conditions.conditionalStatement.IfStatement;
import minipython.builder.cbuilder.lang.conditions.conditionalStatement.WhileStatement;
import minipython.builder.cbuilder.lang.keywords.bool.AndKeyword;
import minipython.builder.cbuilder.lang.keywords.bool.NotKeyword;
import minipython.builder.cbuilder.lang.keywords.bool.OrKeyword;
import minipython.builder.cbuilder.lang.literals.BoolLiteral;
import minipython.builder.cbuilder.lang.literals.IntLiteral;
import minipython.builder.cbuilder.lang.literals.StringLiteral;
import minipython.builder.cbuilder.lang.literals.TupleLiteral;
import minipython.builder.cbuilder.lang.objects.AttributeAssignment;
import minipython.builder.cbuilder.lang.objects.AttributeReference;
import minipython.builder.cbuilder.lang.objects.Call;
import minipython.builder.cbuilder.lang.objects.MPyClass;
import minipython.builder.cbuilder.lang.objects.SuperCall;
import minipython.builder.cbuilder.lang.objects.functions.Argument;
import minipython.builder.cbuilder.lang.objects.functions.Function;
import minipython.builder.cbuilder.lang.objects.functions.ReturnStatement;
import minipython.builder.cbuilder.lang.variables.Assignment;
import minipython.builder.cbuilder.lang.variables.VariableDeclaration;
import minipython.builder.lang.builtins.ClassMPyObject;
import minipython.builder.lang.builtins.FunctionPrint;
import minipython.builder.transform.Transformation;
import minipython.builder.transform.TransformationManager;

/**
 * Transformation of MiniPython programs represented by the generic builder
 * to the representation of the C builder.
 */
public class Transform {

    private class ClassMPyObjectTransform implements Transformation<minipython.builder.lang.builtins.ClassMPyObject, Reference, Transform> {

        @Override
        public Reference apply(ClassMPyObject from, Transform context, TransformationManager manager) {
            return new Reference("__MPyType_Object");
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class FunctionPrintTransform implements Transformation<minipython.builder.lang.builtins.FunctionPrint, Reference, Transform> {

        @Override
        public Reference apply(FunctionPrint from, Transform context, TransformationManager manager) {
            return new Reference("print");
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class IfThenElseStatementTransform implements Transformation<minipython.builder.lang.conditions.IfThenElseStatement, IfThenElseStatement, Transform> {

        @Override
        public IfThenElseStatement apply(minipython.builder.lang.conditions.IfThenElseStatement from, Transform context, TransformationManager manager) {
            IfStatement ifBlock = new IfStatement(
                manager.transform(from.ifBlock().condition(), context, Expression.class),
                from.ifBlock().body().stream().map(e ->
                    manager.transform(e, context, Statement.class)
                ).collect(Collectors.toList())
            );

            Optional<List<ElifStatement>> elifBlocks = from.elifBlocks().map(blocks ->
                blocks.stream().map(block ->
                    new ElifStatement(
                        manager.transform(block.condition(), context, Expression.class),
                        block.body().stream().map(e ->
                            manager.transform(e, context, Statement.class)
                        ).collect(Collectors.toList())
                    )
                ).collect(Collectors.toList())
            );

            Optional<ElseStatement> elseBody = from.elseBody().map(body ->
                new ElseStatement(
                    body.stream().map(e ->
                        manager.transform(e, context, Statement.class)
                    ).collect(Collectors.toList())
                )
            );

            return new IfThenElseStatement(ifBlock, elifBlocks, elseBody);
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class WhileStatementTransform implements Transformation<minipython.builder.lang.conditions.WhileStatement, WhileStatement, Transform> {

        @Override
        public WhileStatement apply(minipython.builder.lang.conditions.WhileStatement from, Transform context, TransformationManager manager) {
            return new WhileStatement(
                manager.transform(from.statement().condition(), context, Expression.class),
                from.statement().body().stream().map(e ->
                    manager.transform(e, context, Statement.class)
                ).collect(Collectors.toList())
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class FunctionDeclaratationTransform implements Transformation<minipython.builder.lang.functions.FunctionDeclaration, Function, Transform> {
        @Override
        public Function apply(minipython.builder.lang.functions.FunctionDeclaration from, Transform context, TransformationManager manager) {
            List<Argument> arguments = new LinkedList<>();
            int argPos = 0;
            for (minipython.builder.lang.variables.VariableDeclaration arg : from.arguments()) {
                arguments.add(new Argument(arg.name(), argPos));
                argPos++;
            }

            return new Function(
                from.name(),
                from.body().stream().map(s -> manager.transform(s, context, Statement.class)).collect(Collectors.toList()),
                arguments,
                from.localVariables().stream().map(v -> manager.transform(v, context, VariableDeclaration.class)).collect(Collectors.toList())
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class ReturnStatementTransform implements Transformation<minipython.builder.lang.functions.ReturnStatement, ReturnStatement, Transform> {
        @Override
        public ReturnStatement apply(minipython.builder.lang.functions.ReturnStatement from, Transform context, TransformationManager manager) {
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
            return new AndKeyword(manager.transform(from.left(), context, Expression.class), manager.transform(from.right(), context, Expression.class));
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
            return new OrKeyword(manager.transform(from.left(), context, Expression.class), manager.transform(from.right(), context, Expression.class));
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class BoolLiteralTransform implements Transformation<minipython.builder.lang.literal.BoolLiteral, BoolLiteral, Transform> {

        @Override
        public BoolLiteral apply(minipython.builder.lang.literal.BoolLiteral from, Transform context, TransformationManager manager) {
            return new BoolLiteral(from.value());
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class IntLiteralTransform implements Transformation<minipython.builder.lang.literal.IntLiteral, IntLiteral, Transform> {

        @Override
        public IntLiteral apply(minipython.builder.lang.literal.IntLiteral from, Transform context, TransformationManager manager) {
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
            return new StringLiteral(from.value());
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
        public AttributeAssignment apply(minipython.builder.lang.object.AttributeAssignment from, Transform context, TransformationManager manager) {
            return new AttributeAssignment(
                manager.transform(from.attribute(), context, AttributeReference.class),
                manager.transform(from.value(), context, Expression.class)
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class AttributeReferenceTransform implements Transformation<minipython.builder.lang.object.AttributeReference, AttributeReference, Transform> {


        @Override
        public AttributeReference apply(minipython.builder.lang.object.AttributeReference from, Transform context, TransformationManager manager) {
            return new AttributeReference(
                from.attributeName(),
                manager.transform(from.object(), context, Expression.class)
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class MPyClassTransform implements Transformation<minipython.builder.lang.object.MPyClass, MPyClass, Transform> {

        @Override
        public MPyClass apply(minipython.builder.lang.object.MPyClass from, Transform context, TransformationManager manager) {
            return new MPyClass(
                    from.name(),
                    manager.transform(from.parent(), context, Expression.class),
                    from.functions().stream().map(f -> manager.transform(f, context, Function.class)).collect(Collectors.toList()),
                    from.classAttributes().entrySet().stream().collect(Collectors.toMap(entry -> new Reference(entry.getKey()), entry -> manager.transform(entry.getValue(), context, Expression.class)))
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class SuperCallTransform implements Transformation<minipython.builder.lang.object.SuperCall, SuperCall, Transform> {

        @Override
        public SuperCall apply(minipython.builder.lang.object.SuperCall from, Transform context, TransformationManager manager) {
            return new SuperCall(
                    from.positionalArgs().stream().map(f -> manager.transform(f, context, Expression.class)).collect(Collectors.toList())
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class AssignmentTransform implements Transformation<minipython.builder.lang.variables.Assignment, Assignment, Transform> {

        @Override
        public Assignment apply(minipython.builder.lang.variables.Assignment from, Transform context, TransformationManager manager) {
            return new Assignment(
                manager.transform(from.lhs(), context, Reference.class),
                manager.transform(from.rhs(), context, Expression.class)
            );
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    private class VariableDeclarationTransform implements Transformation<minipython.builder.lang.variables.VariableDeclaration, Reference, Transform> {

        @Override
        public Reference apply(minipython.builder.lang.variables.VariableDeclaration from, Transform context, TransformationManager manager) {
            return new Reference(from.name());
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

    private class ModuleTransform implements Transformation<minipython.builder.lang.Module, ProgramBuilder, Transform> {

        @Override
        public ProgramBuilder apply(minipython.builder.lang.Module from, Transform context,
                TransformationManager manager) {
            ProgramBuilder builder = new ProgramBuilder();

            // can't use streams here, since Stream#forEach does not
            // guarantee order
            // (https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Ordering)
            // ("Further, some terminal operations may ignore encounter order, such as forEach().")
            for (minipython.builder.lang.Statement stmt : from.body()) {
                builder.addStatement(manager.transform(stmt, context, Statement.class));
            }
            for (minipython.builder.lang.variables.VariableDeclaration decl : from.globalVariabes()) {
                // in the cbuilder API, variable declarations and references
                // are completely disconnected (i.e. variables are referenced
                // by plain name via the Reference class, not by
                // the VariableDeclaration (which does not implement Expression)).
                // Since the transformation API does not allow distinguishing
                // between different target types for the same source class,
                // skip the transformation when a declaration is used as a declaration,
                // and implement the transformation of VariableDeclaration s
                // as a transformation into a reference.
                // This is easier, because declarations are always singled out
                // anyways, while references usually occur as part of a list
                // of expressions(/statements); i.e. if the transformation
                // of VariableDeclaration yielded a VariableDeclaration,
                // all Expression transformations would need to be special cased
                // to catch VariableDeclaration's *before* the transformation.
                builder.addVariable(new VariableDeclaration(decl.name()));
            }
            for (minipython.builder.lang.object.MPyClass clazz : from.classes()) {
                builder.addClass(manager.transform(clazz, context, MPyClass.class));
            }
            for (minipython.builder.lang.functions.FunctionDeclaration func : from.functions()) {
                builder.addFunction(manager.transform(func, context, Function.class));
            }

            return builder;
        }

        @Override
        public Class<Transform> getContextClass() {
            return Transform.class;
        }
    }

    /**
     * Transform a MiniPython program tree to a C/c-runtime based tree.
     *
     * Modifications to \a module after calling this method are *not*
     * visible in the already returned program builder.
     *
     * The current transformation architecture does not support incremental transformation.
     */
    public static ProgramBuilder transform(minipython.builder.lang.Module module) {
        return new Transform().transform_(module);
    }

    // this cannot be part of the static method,
    // since the inner classes can only be instantiated
    // if they have an eclosing instance of Transform
    public ProgramBuilder transform_(minipython.builder.lang.Module module) {
        TransformationManager manager = new TransformationManager();

        manager.registerTransformation(new ClassMPyObjectTransform(), minipython.builder.lang.builtins.ClassMPyObject.class);
        manager.registerTransformation(new FunctionPrintTransform(), minipython.builder.lang.builtins.FunctionPrint.class);
        // note that the ConditionalBlock class has no direct transformation registered,
        // since its conversion target is dependent on its usage context
        // inside the IfThenElseStatement
        manager.registerTransformation(new IfThenElseStatementTransform(), minipython.builder.lang.conditions.IfThenElseStatement.class);
        manager.registerTransformation(new WhileStatementTransform(), minipython.builder.lang.conditions.WhileStatement.class);
        manager.registerTransformation(new FunctionDeclaratationTransform(), minipython.builder.lang.functions.FunctionDeclaration.class);
        manager.registerTransformation(new ReturnStatementTransform(), minipython.builder.lang.functions.ReturnStatement.class);
        manager.registerTransformation(new AndKeywordTransform(), minipython.builder.lang.keyword.AndKeyword.class);
        manager.registerTransformation(new AndKeywordTransform(), minipython.builder.lang.keyword.AndKeyword.class);
        manager.registerTransformation(new NotKeywordTransform(), minipython.builder.lang.keyword.NotKeyword.class);
        manager.registerTransformation(new OrKeywordTransform(), minipython.builder.lang.keyword.OrKeyword.class);
        manager.registerTransformation(new BoolLiteralTransform(), minipython.builder.lang.literal.BoolLiteral.class);
        manager.registerTransformation(new IntLiteralTransform(), minipython.builder.lang.literal.IntLiteral.class);
        manager.registerTransformation(new StringLiteralTransform(), minipython.builder.lang.literal.StringLiteral.class);
        manager.registerTransformation(new TupleLiteralTransform(), minipython.builder.lang.literal.TupleLiteral.class);
        manager.registerTransformation(new AttributeAssignmentTransform(), minipython.builder.lang.object.AttributeAssignment.class);
        manager.registerTransformation(new AttributeReferenceTransform(), minipython.builder.lang.object.AttributeReference.class);
        manager.registerTransformation(new MPyClassTransform(), minipython.builder.lang.object.MPyClass.class);
        manager.registerTransformation(new SuperCallTransform(), minipython.builder.lang.object.SuperCall.class);
        manager.registerTransformation(new AssignmentTransform(), minipython.builder.lang.variables.Assignment.class);
        manager.registerTransformation(new VariableDeclarationTransform(), minipython.builder.lang.variables.VariableDeclaration.class);
        manager.registerTransformation(new CallTransform(), minipython.builder.lang.Call.class);
        manager.registerTransformation(new ModuleTransform(), minipython.builder.lang.Module.class);

        return manager.transform(module, this, ProgramBuilder.class);
    }

}
