package minipython.builder;

import java.util.List;
import java.util.Optional;

/**
 * A block of code, which contains lines and/or blocks of code.
 *
 * @param blockStartComment comment placed before the content of this block
 * @param content lines/blocks contained in this block
 * @param blockEndComment comment placed after the content of this block
 * @param additionalIndent indent to place before comments/content of this block,
 *                          in addition to the indent of the surrounding block
 * @param CommentSyntax how to start the the comments
 * @see minipython.builder.wasm.Block WASM specific variant of this class
 */
public record Block(
    Optional<String> blockStartComment,
    List<BlockContent> content,
    Optional<String> blockEndComment,
    String additionalIndent,
    CommentSyntax commentSyntax
) implements BlockContent {

    public Block(String additionalIndent, CommentSyntax commentSyntax, BlockContent... content) {
        this(Optional.empty(), List.of(content), Optional.empty(), additionalIndent, commentSyntax);
    }

    public Block(String startComment, String endComment, String additionalIndent, CommentSyntax commentSyntax, BlockContent... content) {
        this(Optional.of(startComment), List.of(content), Optional.of(endComment), additionalIndent, commentSyntax);
    }

    public Block(Optional<String> startComment, Optional<String> endComment, String additionalIndent, CommentSyntax commentSyntax, BlockContent... content) {
        this(startComment, List.of(content), endComment, additionalIndent, commentSyntax);
    }

	@Override
	public String toString(String _indent) {
        String indent = _indent + additionalIndent;
        StringBuilder content = new StringBuilder();

        if (blockStartComment.isPresent()) {
            content.append("%s;; %s\n".formatted(indent, blockStartComment.get()));
        }

        for (BlockContent block : this.content) {
            content.append("%s".formatted(block.toString(indent)));
        }

        if (blockEndComment.isPresent()) {
            content.append("%s;; %s\n".formatted(indent, blockEndComment.get()));
        }

        return content.toString();
	}
}
