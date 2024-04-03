package minipython.builder.wasm.lang;

import minipython.builder.BlockContent;

/**
 * Elements that yield WASM code;
 * this WASM code must not add values to the stack.
 */
public interface Statement {

    /**
     * Create the WASM code of the statement represented.
     *
     * @return WASM code that does not add values to the stack.
     */
    public BlockContent buildStatement(Module partOf);

}
