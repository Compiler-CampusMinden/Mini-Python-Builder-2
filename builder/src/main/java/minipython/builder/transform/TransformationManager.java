package minipython.builder.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TransformationManager {

    private Map<Class<?>, Transformation<?, ?, ?>> transformations;
    private Map<Object, Object> transformationCache;

    private Stack<Object> transforming = new Stack<>();

    public TransformationManager() {
        this.transformations = new HashMap<>();
        this.transformationCache = new HashMap<>();
    }

    /**
     * Register a transformation for \a From instances;
     * note that the transformation to use in {@link #transform(Object, Object, Class)}
     * is identified by the concrete class of the \a from object.
     */
    public <From> void registerTransformation(Transformation<From, ?, ?> transformation, Class<From> fromClass) {
        transformations.put(fromClass, transformation);
    }

    /**
     * Convert \a from to an instace of \a To.
     *
     * A matching transformation must have been registerd with
     * {@link #registerTransformation(Transformation, Class)} first.
     */
    public <From, To, Context> To transform(From from, Context context, Class<To> toClass) {
        // NOTE(FW): the toClass parameter really isn't pretty,
        // but apparently Java is really serious about
        // "generics are a compile-time only feature"
        // (i.e. toClass is needed to get a Class object to check
        // the to object against, because Java doesn't allow accessing it
        // from the To parameter)

        // preserve identity of transformed elements
        // (i.e. if theres a function used as a declaration
        // and as a reference, both uses should point
        // to the same (but new, due to transformation) object
        // after the transformation too)
        Object cachedTo = transformationCache.get(from);
        if (cachedTo != null) {
            if (toClass.isInstance(cachedTo)) {
                // Safety: per the if condition,
                // this cast is valid
                @SuppressWarnings("unchecked")
                To to = (To) cachedTo;
                return to;
            } else {
                throw new IllegalStateException("Transformation of '%s' yielded an object of class '%s' the first time, but is now expected to yield an object of class '%s'!".formatted(from, cachedTo.getClass(), toClass));
            }
        }

        Transformation<?, ?, ?> _transformation = transformations.get(from.getClass());
        if (_transformation == null) {
            throw new IllegalStateException("No transformation registered for conversion of class '%s'".formatted(from.getClass()));
        }

        if (transforming.contains(from)) {
            // manual implementation of how toString() is implemented for Object,
            // since it's likely that if the transformation trips over recursion,
            // the toString method of the object being transformed does just that too...
            throw new IllegalStateException("recursion detected when transforming '%s' with '%s'".formatted(from.getClass().getName() + '@' + Integer.toHexString(from.hashCode()), _transformation));
        }
        transforming.push(from);

        if (!_transformation.getContextClass().isInstance(context) && context != null) {
            throw new IllegalArgumentException("The transformation from class '%s' expected a context of class '%s' but got '%s' instead!".formatted(from.getClass(), _transformation.getClass(), context.getClass()));
        }

        // Safety:
        // - To parameter is always valid here, since it's Object - it's validated after applying the transformation; before that point that's simply impossible
        // - From parameter is valid, because the From bound in #registerTransformation ensures that #transformations contains only transformations where the key matches the From parameter of the associated transformation
        // - Context parameter is checked above
        @SuppressWarnings("unchecked")
        Transformation<From, Object, Context> transformation = (Transformation<From, Object, Context>) _transformation;

        transformation.preApply(from, context, this);
        Object to = transformation.apply(from, context, this);
        // don't call postApply just yet;
        // since it's (also) meant to prevent endless recursion,
        // first put the result of the current transformation into the cache,
        // so that recursive transformation of the current from
        // in postApply accesses the cache, and does not apply
        // the transformation again.

        if (toClass.isInstance(to)) {
            // Safety: per the if condition,
            // this cast is valid
            @SuppressWarnings("unchecked")
            To to_ = (To) to;
            transformationCache.put(from, to_);

            // transformation is now cached,
            // postApply can safely be called
            transformation.postApply(from, to_, context, this);

            transforming.pop();

            return to_;
        } else {
            throw new IllegalStateException("The transformation of '%s' yieleded an object of class '%s' which is incompatible with the specified conversion target class '%s'!".formatted(from, to.getClass(), toClass));
        }
    }

}
