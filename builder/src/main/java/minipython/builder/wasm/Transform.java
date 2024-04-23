package minipython.builder.wasm;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import minipython.builder.transform.Transformation;
import minipython.builder.transform.TransformationManager;
import minipython.builder.wasm.lang.Call;
import minipython.builder.wasm.lang.Expression;
import minipython.builder.wasm.lang.MPyModule;
import minipython.builder.wasm.lang.Statement;
import minipython.builder.wasm.lang.builtin.Builtins;
import minipython.builder.wasm.lang.builtin.FunctionPrint;
import minipython.builder.wasm.lang.literal.IntLiteral;
import minipython.builder.wasm.lang.literal.StringLiteral;
import minipython.builder.wasm.lang.literal.TupleLiteral;

/**
 * Transformation of MiniPython programs represented by the generic builder
 * to the representation of the WASM builder.
 */
public class Transform {

    // will be initialised during the module conversion
    private MPyModule module = null;

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
        public StringLiteral apply(minipython.builder.lang.literal.StringLiteral from, Transform context,
                TransformationManager manager) {
            return context.module.newString(from.value());
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

    private class ModuleTransform implements Transformation<minipython.builder.lang.MPyModule, MPyModule, Transform> {

        @Override
        public MPyModule apply(minipython.builder.lang.MPyModule from, Transform context,
                TransformationManager manager) {
            // the module is needed by some transformations as context
            // (e.g. stringliteral)
            // inject this module instance here
            List<Statement> module = new LinkedList<>();
            MPyModule mod = new MPyModule(module);
            context.module = mod;

            // can't use streams here, since Stream#forEach does not
            // guarantee order
            // (https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Ordering)
            // ("Further, some terminal operations may ignore encounter order, such as forEach().")
            for (minipython.builder.lang.Statement stmt : from.body()) {
                module.add(manager.transform(stmt, context, Statement.class));
            }

            return mod;
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
        manager.registerTransformation(new IntLiteralTransform(), minipython.builder.lang.literal.IntLiteral.class);
        manager.registerTransformation(new StringLiteralTransform(), minipython.builder.lang.literal.StringLiteral.class);
        manager.registerTransformation(new TupleLiteralTransform(), minipython.builder.lang.literal.TupleLiteral.class);
        manager.registerTransformation(new CallTransform(), minipython.builder.lang.Call.class);
        manager.registerTransformation(new ModuleTransform(), minipython.builder.lang.MPyModule.class);

        return manager.transform(module, this, MPyModule.class);
    }
}
