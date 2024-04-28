package minipython.builder.wasm.lang;

import minipython.builder.BlockContent;

/**
 * Elements that yield WASM code;
 * this WASM code must leave a pointer to a MiniPython object on the stack.
 */
public interface Expression extends Statement {

    /**
     * Create the WASM code of the expression represented.
     *
     * @return WASM code that leaves a pointer to a MiniPython object on the stack.
     */
    public BlockContent buildExpression(MPyModule partOf);

}
