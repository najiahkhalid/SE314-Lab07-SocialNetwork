package twitter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 */
public class Extract {

    // An @-mention is '@' followed by a username, where the '@' is not preceded
    // by a username character (so "bitdiddle@mit.edu" is not a mention).
    private static final Pattern MENTION =
            Pattern.compile("(?<![A-Za-z0-9_-])@([A-Za-z0-9_-]+)");

    /**
     * Get usernames mentioned in a list of tweets.
     *
     * @param tweets list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username, where the
     *         "@" is not preceded and the username is not followed by a valid
     *         username character. Usernames are case-insensitive; the returned
     *         set contains each username in lower case.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentioned = new HashSet<>();
        for (Tweet tweet : tweets) {
            Matcher m = MENTION.matcher(tweet.getText());
            while (m.find()) {
                mentioned.add(m.group(1).toLowerCase());
            }
        }
        return mentioned;
    }
}
