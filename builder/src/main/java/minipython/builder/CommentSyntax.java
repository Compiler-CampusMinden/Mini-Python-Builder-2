package minipython.builder;

/**
 * Comment syntaxes for code generation with \a Block and \a Line.
 *
 * @see Block
 * @see Line
 */
public enum CommentSyntax {
    COMMENT_SYNTAX_C("//"),
    COMMENT_SYNTAX_WASM(";;");

    /**
     * How to start a comment that runs until end of the current line.
     */
    public final String commentStart;

    CommentSyntax(String commentStart) {
        this.commentStart = commentStart;
    }
}
