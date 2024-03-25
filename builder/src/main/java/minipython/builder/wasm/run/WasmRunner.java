package minipython.builder.wasm.run;

import java.io.IOException;

public interface WasmRunner {
    public void run(String wat) throws Exception;
}
