package minipython.builder.wasm.run;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import dev.dirs.ProjectDirectories;

public class CruntimeWasm {
    private final Path libraryPath;

    public CruntimeWasm() throws IOException {
        String libHash;
        try (InputStream in = getClass().getResourceAsStream("/lib/mpy_cruntime.wasm.sha256")) {
            // \A = beginning of input, i.e. the whole stream as a single token
            try (Scanner scanner = new Scanner(in).useDelimiter("\\A")) {
                libHash = scanner.hasNext() ? scanner.next() : "";
            }
        }

        ProjectDirectories dirs = ProjectDirectories.from(null, null, "minipython");
        libraryPath = Paths.get(dirs.cacheDir, "lib_mpy_cruntime", libHash, "mpy_cruntime.wasm");

        if (!libraryPath.toFile().exists()) {
            Files.createDirectories(libraryPath.getParent());
            try (InputStream in = getClass().getResourceAsStream("/lib/mpy_cruntime.wasm")) {
                Files.copy(in, libraryPath);
            }
        }
    }

    public Path getPath() {
        return this.libraryPath;
    }
}
