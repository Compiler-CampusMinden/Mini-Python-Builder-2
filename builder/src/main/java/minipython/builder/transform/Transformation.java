package minipython.builder.transform;

/**
 * A transformation describing how to convert instances of \a From to instances of \a To; using a context instance of class \a Context.
 */
public interface Transformation<From, To, Context> {

    /**
     * Apply this transformation; note that transformations are only identified by \a From, and uses the concrete class of the \a from object.
     */
    public To apply(From from, Context context, TransformationManager manager);

    /**
     * The expected class of \a Context objects passed to {@link #apply(Object, Object, TransformationManager)}.
     */
    public Class<Context> getContextClass();
}
