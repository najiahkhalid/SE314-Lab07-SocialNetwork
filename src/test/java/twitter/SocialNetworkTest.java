package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * Testing strategy
     *
     * guessFollowsGraph(tweets):
     *   partition on number of tweets: 0, 1, > 1
     *   partition on mentions in a tweet: 0, 1, > 1
     *   partition on tweets per author: 1, > 1
     *   partition on username case: same case, different case
     *   Because the postcondition is underdetermined (an author with no
     *   evidence may be absent OR map to an empty set), tests only check
     *   that the required follows-edges exist, comparing usernames
     *   case-insensitively, and never require a specific set of keys.
     *
     * influencers(followsGraph):
     *   partition on number of users: 0, 1, > 1
     *   partition on followers: user has 0 followers, user has >= 1
     *   partition on follower counts: all distinct, some tied
     *   For ties, any order between tied users is allowed, so tests only
     *   check that tied users come before less-followed users.
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "ernie", "hey @bert how are you?", d2);
    private static final Tweet tweet3 = new Tweet(3, "ernie", "@bert @elmo let's play", d2);
    private static final Tweet tweet4 = new Tweet(4, "ernie", "@grover see you soon", d2);
    private static final Tweet tweet5 = new Tweet(5, "Ernie", "@BERT are you there?", d2);

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    /** Returns the set of users that user follows in graph, ignoring case. */
    private static Set<String> follows(Map<String, Set<String>> graph, String user) {
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Set<String>> e : graph.entrySet()) {
            if (e.getKey().equalsIgnoreCase(user)) {
                for (String f : e.getValue()) {
                    result.add(f.toLowerCase());
                }
            }
        }
        return result;
    }

    /** Returns true if every user in graph follows nobody. */
    private static boolean noEdges(Map<String, Set<String>> graph) {
        for (Set<String> s : graph.values()) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static List<String> lower(List<String> users) {
        List<String> result = new ArrayList<>();
        for (String u : users) {
            result.add(u.toLowerCase());
        }
        return result;
    }

    // ---------------------------------------------------------------
    // guessFollowsGraph()
    // ---------------------------------------------------------------

    // 1. Empty list of tweets -- covers: 0 tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());

        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    // 2. Tweets without mentions -- covers: 1 tweet, 0 mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(List.of(tweet1));

        assertTrue("expected no follows-edges", noEdges(followsGraph));
    }

    // 3. Single mention -- covers: 1 tweet, 1 mention
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(List.of(tweet2));

        assertEquals(Set.of("bert"), follows(followsGraph, "ernie"));
    }

    // 4. Multiple mentions -- covers: 1 tweet, > 1 mentions
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(List.of(tweet3));

        assertEquals(Set.of("bert", "elmo"), follows(followsGraph, "ernie"));
    }

    // 5. Multiple tweets from one user -- covers: > 1 tweets per author, different case
    @Test
    public void testGuessFollowsGraphMultipleTweetsOneUser() {
        Map<String, Set<String>> followsGraph =
                SocialNetwork.guessFollowsGraph(List.of(tweet2, tweet3, tweet4, tweet5));

        assertEquals(Set.of("bert", "elmo", "grover"), follows(followsGraph, "ernie"));
    }

    // ---------------------------------------------------------------
    // influencers()
    // ---------------------------------------------------------------

    // 6. Empty graph -- covers: 0 users
    @Test
    public void testInfluencersEmpty() {
        List<String> influencers = SocialNetwork.influencers(new HashMap<>());

        assertTrue("expected empty list", influencers.isEmpty());
    }

    // 7. Single user without followers -- covers: 1 user, 0 followers
    @Test
    public void testInfluencersSingleUserNoFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<>());

        List<String> influencers = SocialNetwork.influencers(followsGraph);

        // the spec lists every user, so alyssa appears once with 0 followers
        // and nobody else is reported as an influencer
        assertEquals(List.of("alyssa"), lower(influencers));
    }

    // 8. Single influencer -- covers: > 1 users, one user with >= 1 follower
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("ernie", Set.of("bert"));

        List<String> influencers = lower(SocialNetwork.influencers(followsGraph));

        assertEquals(2, influencers.size());
        assertEquals("bert", influencers.get(0));
        assertTrue(influencers.contains("ernie"));
    }

    // 9. Multiple influencers -- covers: > 1 users, all follower counts distinct
    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("ernie", Set.of("bert", "elmo"));
        followsGraph.put("grover", Set.of("bert"));
        // bert: 2 followers, elmo: 1, ernie: 0, grover: 0

        List<String> influencers = lower(SocialNetwork.influencers(followsGraph));

        assertEquals(4, influencers.size());
        assertEquals("bert", influencers.get(0));
        assertEquals("elmo", influencers.get(1));
    }

    // 10. Tied influence -- covers: > 1 users, tied follower counts
    @Test
    public void testInfluencersTiedInfluence() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("ernie", Set.of("bert", "elmo"));
        followsGraph.put("grover", Set.of("bert", "elmo"));
        // bert and elmo: 2 followers each (tie), ernie and grover: 0

        List<String> influencers = lower(SocialNetwork.influencers(followsGraph));

        assertEquals(4, influencers.size());
        assertEquals(Set.of("bert", "elmo"), new HashSet<>(influencers.subList(0, 2)));
    }
}
