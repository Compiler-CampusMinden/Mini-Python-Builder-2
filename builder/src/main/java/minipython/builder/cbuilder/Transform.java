package minipython.builder.cbuilder;

import java.util.stream.Collectors;

import minipython.builder.cbuilder.lang.Expression;
import minipython.builder.cbuilder.lang.ProgramBuilder;
import minipython.builder.cbuilder.lang.Reference;
import minipython.builder.cbuilder.lang.Statement;
import minipython.builder.cbuilder.lang.literals.IntLiteral;
import minipython.builder.cbuilder.lang.literals.StringLiteral;
import minipython.builder.cbuilder.lang.literals.TupleLiteral;
import minipython.builder.cbuilder.lang.objects.Call;
import minipython.builder.lang.builtins.FunctionPrint;
import minipython.builder.transform.Transformation;
import minipython.builder.transform.TransformationManager;

/**
 * Transformation of MiniPython programs represented by the generic builder
 * to the representation of the C builder.
 */
public class Transform {

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

        manager.registerTransformation(new FunctionPrintTransform(), minipython.builder.lang.builtins.FunctionPrint.class);
        manager.registerTransformation(new IntLiteralTransform(), minipython.builder.lang.literal.IntLiteral.class);
        manager.registerTransformation(new StringLiteralTransform(), minipython.builder.lang.literal.StringLiteral.class);
        manager.registerTransformation(new TupleLiteralTransform(), minipython.builder.lang.literal.TupleLiteral.class);
        manager.registerTransformation(new CallTransform(), minipython.builder.lang.Call.class);
        manager.registerTransformation(new ModuleTransform(), minipython.builder.lang.Module.class);

        return manager.transform(module, this, ProgramBuilder.class);
    }

}
