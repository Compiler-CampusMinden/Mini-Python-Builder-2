package minipython.builder.wasm;

import java.util.List;
import java.util.Optional;

import minipython.builder.BlockContent;
import minipython.builder.CommentSyntax;

/**
 * @see minipython.builder.Block language indepent variant of this class
 */
public class Block implements BlockContent {

    public final minipython.builder.Block inner;

    public Block(Optional<String> blockStartComment, List<BlockContent> content, Optional<String> blockEndComment, String additionalIndent) {
        this.inner = new minipython.builder.Block(blockStartComment, content, blockEndComment, additionalIndent, CommentSyntax.COMMENT_SYNTAX_WASM);
    }

    public Block(String additionalIndent, BlockContent... content) {
        this(Optional.empty(), List.of(content), Optional.empty(), additionalIndent);
    }

    public Block(String startComment, String endComment, String additionalIndent, BlockContent... content) {
        this(Optional.of(startComment), List.of(content), Optional.of(endComment), additionalIndent);
    }

    public Block(Optional<String> startComment, Optional<String> endComment, String additionalIndent, BlockContent... content) {
        this(startComment, List.of(content), endComment, additionalIndent);
    }

	@Override
	public String toString(String indent) {
        return inner.toString(indent);
	}

}
