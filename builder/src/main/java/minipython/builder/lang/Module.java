package minipython.builder.lang;

import java.util.List;

/**
 * The top-level element of a MiniPython program.
 */
public record Module(
    List<Statement> body
) {
}
