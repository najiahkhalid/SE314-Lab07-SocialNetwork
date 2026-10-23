package twitter;

import java.time.Instant;

/**
 * Immutable datatype representing a tweet.
 */
public class Tweet {

    private final long id;
    private final String author;
    private final String text;
    private final Instant timestamp;

    /**
     * Make a Tweet.
     *
     * @param id        unique identifier for the tweet
     * @param author    Twitter username of the author, nonempty, only letters, digits, underscore, hyphen
     * @param text      text of the tweet, at most 140 characters
     * @param timestamp date/time when the tweet was sent
     */
    public Tweet(long id, String author, String text, Instant timestamp) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "(" + id + " " + timestamp + " " + author + ") " + text;
    }
}
