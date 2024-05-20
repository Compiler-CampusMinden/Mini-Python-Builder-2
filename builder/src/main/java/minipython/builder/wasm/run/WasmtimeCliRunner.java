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

        WasmtimeCli cli = new WasmtimeCli(getSystem());
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

    /**
     * Assemble an architecture string conforming to the naming scheme
     * of wasmtime binaries (ie `<arch>-<os>`, eg x86_64-windows).
     */
    private static String getSystem() {
        String os = System.getProperty("os.name");
        // os.name is not standardized,
        // best effort attempt to make sure
        // known OSs can be recognized by normalizing them
        os = os.toLowerCase().replaceAll("[^a-z0-9]+", "");
        if (os.contains("linux")) {
            os = "linux";
        } else if (os.contains("win")) {
            os = "windows";
        } else if (os.contains("mac") || os.contains("osx")) {
            os = "macos";
        } else {
            throw new IllegalStateException("Unknown or unsupported OS: '%s'".formatted(System.getProperty("os.name")));
        }

        String arch = System.getProperty("os.arch");
        // os.arch is not standardized,
        // best effort attempt to make sure
        // known arches can be recognized by normalizing them
        arch = arch.toLowerCase().replaceAll("[^a-z0-9]+", "");
        // no check for x86_64 necessary, normalizing removes
        // everything but letters from the alphabet & numbers
        if (arch.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
            arch = "x86_64";
        } else if (arch.equals("aarch64")) {
            arch = "aarch64";
        } else {
            throw new IllegalStateException("Unknown or unsupported arch: '%s'".formatted(System.getProperty("os.arch")));
        }

        return "%s-%s".formatted(arch, os);
    }
}
