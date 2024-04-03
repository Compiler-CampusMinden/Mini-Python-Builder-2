package minipython.builder;

import java.util.Optional;

/**
 * A line of code, optionally ending with a comment.
 *
 * @param content code of this line
 * @param comment comment placed after code of this line
 * @param CommentSyntax how to start the \a comment after \a content
 * @see minipython.builder.wasm.Line WASM specific variant of this class
 */
public record Line(
    String content,
    Optional<String> comment,
    CommentSyntax commentSyntax
) implements BlockContent {

    public Line(String content, CommentSyntax commentSyntax) {
        this(content, Optional.empty(), commentSyntax);
    }

    public Line(String content, String comment, CommentSyntax commentSyntax) {
        this(content, Optional.of(comment), commentSyntax);
    }

	@Override
	public String toString(String indent) {
        String comment;
        String commentSyntax = this.commentSyntax.commentStart;

        if (this.comment.isPresent()) {
            comment = " %s %s".formatted(commentSyntax, this.comment.get());
        } else {
            comment = "";
        }

        return "%s%s%s\n".formatted(indent, content, comment);
	}
}
