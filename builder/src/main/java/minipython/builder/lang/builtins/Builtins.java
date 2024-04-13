package minipython.builder.lang.builtins;

/**
 * Instances of classes representing functionality provided by the runtime as so called built-ins.
 */
public class Builtins {

    public final static FunctionPrint FUNCTION_PRINT = new FunctionPrint();
    public final static ClassMPyObject CLASS_MPY_OBJECT = new ClassMPyObject();
}
