import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.nio.file.*;

public class CacheManager {

    private static final String CACHE_FILE = ".review-cache";
    private Map<String, ReviewResult> memoryCache;

    public CacheManager() {
        this.memoryCache = new HashMap<>();
        loadFromDisk();
    }

    /**
     * Checks if a review for this file hash already exists.
     * Avoids redundant API calls for unchanged files.
     */
    public boolean contains(String fileHash) {
        return memoryCache.containsKey(fileHash);
    }

    public ReviewResult get(String fileHash) {
        return memoryCache.get(fileHash);
    }

    public void put(String fileHash, ReviewResult result) {
        memoryCache.put(fileHash, result);
        saveToDisk();
    }

    /**
     * Persists cache to disk so reviews survive between runs.
     * Cache is stored in the project root as a hidden file.
     */
    private void saveToDisk() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CACHE_FILE))) {
            oos.writeObject(memoryCache);
        } catch (Exception e) {
            System.err.println("Failed to save cache: " + e.getMessage());
        }
    }

    private void loadFromDisk() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(CACHE_FILE))) {
            memoryCache = (HashMap<String, ReviewResult>) ois.readObject();
        } catch (FileNotFoundException e) {
            // No cache file yet, start fresh
        } catch (Exception e) {
            System.err.println("Failed to load cache: " + e.getMessage());
        }
    }

    public void clearCache() {
        memoryCache.clear();
        try {
            Files.deleteIfExists(Paths.get(CACHE_FILE));
        } catch (Exception e) {
            System.err.println("Failed to clear cache: " + e.getMessage());
        }
    }
}
