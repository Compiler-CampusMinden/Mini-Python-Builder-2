package minipython.builder;

/**
 * Elements that are part of a \a Block.
 *
 * @see Block
 * @see Line
 */
public interface BlockContent {
    /**
     * Convert this block content to a string,
     * using \a indent as the base indentation of lines.
     *
     * @param indent The indentation of the surrounding block.
     */
    public String toString(String indent);
}
