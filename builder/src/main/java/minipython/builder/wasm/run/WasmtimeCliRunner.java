package minipython.builder.wasm.run;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class WasmtimeCliRunner implements WasmRunner {

	@Override
	public void run(String wat) throws Exception {
        Path watFile = Files.createTempFile("mpy_builder_output", ".wat");
        try (OutputStream _out = Files.newOutputStream(watFile)) {
            try (PrintStream out = new PrintStream(_out, false, "UTF-8")) {
                out.print(wat);
            }
        }

        WasmtimeCli cli = new WasmtimeCli("x86_64-linux");
        CruntimeWasm cruntime = new CruntimeWasm();

        String cliPath = System.getenv("MPY_BUILDER_WASM_USE_SYSTEM_WASMTIME") != null ? "wasmtime" : cli.getPath().toString();

        // wasmtime run --invoke mpy__main__ --preload mpy_runtime=c-runtime/lib/mpy_cruntime.wasm playground.wat
        ProcessBuilder wasmtime = new ProcessBuilder(cliPath, "run", "--invoke", "mpy__main__", "--preload", "mpy_runtime=" + cruntime.getPath(), watFile.toString());
        wasmtime.inheritIO();

        Process p = wasmtime.start();
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new Exception("Failed to run wat code, wasmtime exited with code " + exitCode + ". generated wat code at '" + watFile + "'");
        }
        // run was successful - no need to keep the file
        watFile.toFile().delete();
	}
}
