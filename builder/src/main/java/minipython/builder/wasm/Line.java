package minipython.builder.wasm;

import java.util.Optional;

import minipython.builder.BlockContent;
import minipython.builder.CommentSyntax;

/**
 * @see minipython.builder.Line language indepent variant of this class
 */
public class Line implements BlockContent {

    public final minipython.builder.Line inner;

    public Line(String content, Optional<String> comment) {
        this.inner = new minipython.builder.Line(content, comment, CommentSyntax.COMMENT_SYNTAX_WASM);
    }

    public Line(String content) {
        this(content, Optional.empty());
    }

    public Line(String content, String comment) {
        this(content, Optional.of(comment));
    }

	@Override
	public String toString(String indent) {
        return inner.toString(indent);
	}

}

