package minipython.builder.lang;

public enum Scope {
    /**
     * The global (module, in python/wasm) scope.
     */
    SCOPE_GLOBAL,
    /**
     * Any non-global scope.
     */
    SCOPE_LOCAL;
}

