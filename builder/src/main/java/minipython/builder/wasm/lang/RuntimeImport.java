package minipython.builder.wasm.lang;

import java.util.List;
import java.util.Optional;

/**
 * Import of a function from the c-runtime library.
 *
 * The \a functionName must match the name of the function
 * in the c-runtime library and will be used
 * as the identifier of the function inside the WASM module
 * (prepended with '$').
 *
 * @param functionName name of the function
 * @param parameters parameters declared by the function
 * @param returnType return type declared by the function
 */
public record RuntimeImport(
    String functionName,
    List<RuntimeImportType> parameters,
    Optional<RuntimeImportType> returnType
) {
    /**
     * WASM types.
     */
    public enum RuntimeImportType {
        I32("i32"),
        I64("i64");

        public final String wasm;

        RuntimeImportType(String wasm) {
            this.wasm = wasm;
        }
    }

    /**
     * @return WAT code to import this function.
     */
    public String build() {
        StringBuilder parameterDecl = new StringBuilder();
        if (parameters.size() > 0) {
            parameterDecl.append(" (param");
            for (RuntimeImportType param : parameters) {
                parameterDecl.append(" " + param.wasm);
            }
            parameterDecl.append(")");
        }

        String returnDecl = returnType.isPresent() ? " (result %s)".formatted(returnType.get().wasm) : "";

        return """
                (import "mpy_runtime" "%s" (func $%s%s%s))
        """.formatted(functionName, functionName, parameterDecl.toString(), returnDecl).trim();
    }
}

