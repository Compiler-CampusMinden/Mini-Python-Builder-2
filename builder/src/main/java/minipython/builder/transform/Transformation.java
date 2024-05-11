package minipython.builder.transform;

/**
 * A transformation describing how to convert instances of \a From to instances of \a To; using a context instance of class \a Context.
 */
public interface Transformation<From, To, Context> {

    /**
     * Called before this transformation is applied.
     */
    default public void preApply(From from, Context context, TransformationManager manager) {
    }

    /**
     * Apply this transformation; note that transformations are only identified by \a From, and uses the concrete class of the \a from object.
     */
    public To apply(From from, Context context, TransformationManager manager);

    /**
     * Called with the transformation result. after this transformation has been applied.
     */
    default public void postApply(From from, To to, Context context, TransformationManager manager) {
    }

    /**
     * The expected class of \a Context objects passed to \a {@link #apply(Object, Object, TransformationManager)}.
     */
    public Class<Context> getContextClass();
}
