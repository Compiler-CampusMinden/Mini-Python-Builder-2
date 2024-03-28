package minipython.builder.wasm.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import minipython.builder.wasm.Block;
import minipython.builder.BlockContent;
import minipython.builder.wasm.Line;

/**
 * The top-level element of a MiniPython program.
 */
public class Module {
    private final List<Statement> body;
    private Set<RuntimeImport> importedRuntimeFunctions = new HashSet<>();

    /**
     * @param body statements in the global scope of the MiniPython program
     */
    public Module(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return this.body;
    }

    /**
     * (Internal) \a functionName is used by generated WASM code and must be imported.
     */
    public void declareRuntimeImport(RuntimeImport functionName) {
        this.importedRuntimeFunctions.add(functionName);
    }

    /**
     * (Internal) \a functionNames are used by generated WASM code and must be imported.
     */
    public void declareRuntimeImports(RuntimeImport... functionNames) {
        this.importedRuntimeFunctions.addAll(Arrays.asList(functionNames));
    }

    /**
     * Build the WASM code corresponding to the program represented by this instance.
     */
    public String build() {
        BlockContent body = body();
        // this must be called last,
        // as the imports are collected when building
        // the statements/expressions
        BlockContent imports = imports();

        return new Block("",
            new Line("(module"),
            new Block(
                "  ",
                imports,
                new Block(
                    Optional.of("this function represents execution of statements in global (mini)python scope"),
                    Optional.empty(),
                    "",
                    new Line("(func (export \"mpy__main__\")"),
                    body,
                    new Line(")")
                )
            ),
            new Line(")")
        ).toString("");
    }

    /**
     * @return WAT import statements.
     */
    private BlockContent imports() {
        List<BlockContent> imports = new ArrayList<>();
        for (RuntimeImport fn : importedRuntimeFunctions) {
            imports.add(new Line(fn.build()));
        }

        return new Block(Optional.of("imports from c-runtime library"), imports, Optional.empty(), "");
    }

    /**
     * @return A WAT function body, containing the statements of the global scope of this program.
     */
    private BlockContent body() {
        List<BlockContent> body = new ArrayList<>();

        for (Statement stmt : this.body) {
            body.add(stmt.buildStatement(this));
        }

        return new Block(Optional.empty(), body, Optional.empty(), "  ");
    }
}
