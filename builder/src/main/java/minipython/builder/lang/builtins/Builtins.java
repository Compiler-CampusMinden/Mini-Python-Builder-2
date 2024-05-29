package minipython.builder.lang.builtins;

/**
 * Instances of classes representing functionality provided by the runtime as so called built-ins.
 */
public class Builtins {

    public final static FunctionPrint FUNCTION_PRINT = new FunctionPrint();
    public final static FunctionSuper FUNCTION_SUPER = new FunctionSuper();
    public final static FunctionType FUNCTION_TYPE = new FunctionType();
    public final static FunctionId FUNCTION_ID = new FunctionId();
    public final static FunctionInput FUNCTION_INPUT = new FunctionInput();
    public final static ClassMPyObject CLASS_MPY_OBJECT = new ClassMPyObject();
    public final static ClassMPyNum CLASS_MPY_NUM = new ClassMPyNum();
    public final static ClassMPyTuple CLASS_MPY_TUPLE = new ClassMPyTuple();
    public final static ClassMPyFunction CLASS_MPY_FUNCTION = new ClassMPyFunction();
    public final static ClassMPyBoundMethod CLASS_MPY_BOUND_METHOD = new ClassMPyBoundMethod();
    public final static ClassMPyNone CLASS_MPY_NONE = new ClassMPyNone();
    public final static ClassMPyType CLASS_MPY_TYPE = new ClassMPyType();
    public final static ClassMPyStr CLASS_MPY_STR = new ClassMPyStr();
    public final static ClassMPyBoolean CLASS_MPY_BOOLEAN = new ClassMPyBoolean();
}
