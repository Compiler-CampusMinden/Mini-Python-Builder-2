package minipython.builder.wasm.lang.builtin;

/**
 * Instances of classes representing functionality provided by the runtime as so called built-ins.
 */
public class Builtins {

    public final static FunctionPrint FUNCTION_PRINT = new FunctionPrint();
    public final static FunctionSuper FUNCTION_SUPER = new FunctionSuper();
    public final static FunctionType FUNCTION_TYPE = new FunctionType();
    public final static FunctionId FUNCTION_ID = new FunctionId();
    public final static FunctionInput FUNCTION_INPUT = new FunctionInput();
    public final static TypeObject TYPE_OBJECT = new TypeObject();
    public final static TypeNum TYPE_NUM = new TypeNum();
    public final static TypeTuple TYPE_TUPLE = new TypeTuple();
    public final static TypeFunction TYPE_FUNCTION = new TypeFunction();
    public final static TypeBoundMethod TYPE_BOUND_METHOD = new TypeBoundMethod();
    public final static TypeNone TYPE_NONE = new TypeNone();
    public final static TypeType TYPE_TYPE = new TypeType();
    public final static TypeStr TYPE_STR = new TypeStr();
    public final static TypeBoolean TYPE_BOOLEAN = new TypeBoolean();

}
