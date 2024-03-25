package minipython.builder.wasm.run;

import java.io.InputStream;
import java.util.Scanner;

public class SimpleMpyWat {
    public static void main(String[] args) throws Exception {
        String libHash;
        try (InputStream in = SimpleMpyWat.class.getResourceAsStream("/simple_mpy.wat")) {
            // \A = beginning of input, i.e. the whole stream as a single token
            try (Scanner scanner = new Scanner(in).useDelimiter("\\A")) {
                libHash = scanner.hasNext() ? scanner.next() : "";
            }
        }

        new WasmtimeCliRunner().run(libHash);
    }
}
