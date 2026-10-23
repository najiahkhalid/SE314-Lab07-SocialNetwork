package twitter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds a social network from a sample of tweets and prints the
 * top 10 most-followed users.
 *
 * Note: the original MIT tweet server (courses.csail.mit.edu) is offline,
 * so this version uses a built-in sample of tweets.
 */
public class Main {

    private static final int SAMPLE_SIZE = 10;

    public static void main(String[] args) {
        List<Tweet> tweets = sampleTweets();
        System.out.println("Read " + tweets.size() + " tweets.");

        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        System.out.println("Follows graph: " + followsGraph);

        List<String> influencers = SocialNetwork.influencers(followsGraph);
        System.out.println("Top " + SAMPLE_SIZE + " influencers:");
        for (String username : influencers.subList(0, Math.min(SAMPLE_SIZE, influencers.size()))) {
            System.out.println("  " + username);
        }
    }

    private static List<Tweet> sampleTweets() {
        Instant t = Instant.parse("2026-10-23T09:00:00Z");
        String[][] data = {
            {"alyssa",     "is it reasonable to talk about rivest so much? @bbitdiddle"},
            {"bbitdiddle", "rivest talk in 30 minutes #hype @alyssa @MIT"},
            {"ernie",      "@Bert have you seen @Elmo today?"},
            {"grover",     "@bert @elmo @mit lunch at noon"},
            {"elmo",       "@BERT tickle me later"},
            {"cookie",     "me want cookie @Bert @mit"},
            {"oscar",      "go away, all of you"},
            {"bert",       "@ernie please clean up the room"},
            {"kermit",     "it is not easy being green @MIT @alyssa"},
            {"piggy",      "moi? @kermit"},
            {"fozzie",     "wocka wocka @kermit @bert"},
            {"gonzo",      "email me at gonzo@muppets.com"},
        };
        Tweet[] tweets = new Tweet[data.length];
        for (int i = 0; i < data.length; i++) {
            tweets[i] = new Tweet(i + 1, data[i][0], data[i][1], t.plusSeconds(60L * i));
        }
        return List.of(tweets);
    }
}
