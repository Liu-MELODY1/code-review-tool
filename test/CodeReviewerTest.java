import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.List;

public class CodeReviewerTest {

    private CacheManager cache;
    private HashUtil hashUtil;

    @Before
    public void setUp() {
        cache = new CacheManager();
    }

    // ===== HashUtil Tests =====

    @Test
    public void testHashIsDeterministic() {
        // Same content should always produce same hash
        String hash1 = HashUtil.computeHashFromString("public class Foo {}");
        String hash2 = HashUtil.computeHashFromString("public class Foo {}");
        assertEquals(hash1, hash2);
    }

    @Test
    public void testDifferentContentProducesDifferentHash() {
        String hash1 = HashUtil.computeHashFromString("public class Foo {}");
        String hash2 = HashUtil.computeHashFromString("public class Bar {}");
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testHashIsNotNull() {
        String hash = HashUtil.computeHashFromString("some code content");
        assertNotNull(hash);
    }

    // ===== CacheManager Tests =====

    @Test
    public void testCacheMissOnNewHash() {
        // A hash that was never stored should return false
        assertFalse(cache.contains("nonexistent-hash-12345"));
    }

    @Test
    public void testCacheHitAfterStore() {
        String fakeHash = "abc123";
        ReviewResult result = ReviewResult.empty();

        cache.put(fakeHash, result);

        assertTrue(cache.contains(fakeHash));
    }

    @Test
    public void testCacheReturnsCorrectResult() {
        String fakeHash = "xyz789";
        ReviewResult result = ReviewResult.empty();

        cache.put(fakeHash, result);
        ReviewResult retrieved = cache.get(fakeHash);

        assertNotNull(retrieved);
    }

    @Test
    public void testCacheClear() {
        cache.put("some-hash", ReviewResult.empty());
        cache.clearCache();
        assertFalse(cache.contains("some-hash"));
    }

    // ===== ReviewResult Tests =====

    @Test
    public void testEmptyResultHasZeroIssues() {
        ReviewResult result = ReviewResult.empty();
        assertEquals(0, result.getTotalIssues());
    }

    @Test
    public void testEmptyResultListsAreNotNull() {
        ReviewResult result = ReviewResult.empty();
        assertNotNull(result.getBugs());
        assertNotNull(result.getPerformanceIssues());
        assertNotNull(result.getStyleIssues());
    }

    // ===== GitDiffParser Tests =====

    @Test
    public void testOnlyJavaFilesAreReturned() {
        // Parser should filter out non-Java files like .xml, .md, .json
        List<String> files = GitDiffParser.getChangedFiles(".");
        for (String file : files) {
            assertTrue(file.endsWith(".java"));
        }
    }
}
