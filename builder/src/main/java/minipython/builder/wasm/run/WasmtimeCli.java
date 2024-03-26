package minipython.builder.wasm.run;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Scanner;
import java.util.Set;

import dev.dirs.ProjectDirectories;

public class WasmtimeCli {
    private final Path executablePath;

    public WasmtimeCli(String system) throws IOException {
        String cliHash;
        try (InputStream in = getClass().getResourceAsStream("/wasmtime-exes/" + system + ".sha256")) {
            // \A = beginning of input, i.e. the whole stream as a single token
            try (Scanner scanner = new Scanner(in).useDelimiter("\\A")) {
                cliHash = scanner.hasNext() ? scanner.next() : "";
            }
        }

        String cliName = system.contains("windows") ? "wasmtime.exe" : "wasmtime";

        ProjectDirectories dirs = ProjectDirectories.from(null, null, "minipython");
        executablePath = Paths.get(dirs.cacheDir, "wasmtime-cli", cliHash, cliName);

        if (!executablePath.toFile().exists()) {
            Files.createDirectories(executablePath.getParent());
            try (InputStream in = getClass().getResourceAsStream("/wasmtime-exes/" + system + "/" + cliName)) {
                Files.copy(in, executablePath);
            }
            executablePath.toFile().setExecutable(true);
        }
    }

    public Path getPath() {
        return this.executablePath;
    }
}
