package minipython.builder.wasm.lang.builtin;

/**
 * Instances of classes representing functionality provided by the runtime as so called built-ins.
 */
public class Builtins {

    public final static FunctionPrint FUNCTION_PRINT = new FunctionPrint();
    public final static FunctionSuper FUNCTION_SUPER = new FunctionSuper();
    public final static TypeObject TYPE_OBJECT = new TypeObject();

}
