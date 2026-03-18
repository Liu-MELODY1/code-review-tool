import java.io.FileInputStream;
import java.security.MessageDigest;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HashUtil {

    /**
     * Computes SHA-256 hash of a file's content.
     * Used as cache key to detect whether a file has changed since last review.
     * More reliable than timestamps since file content is what actually matters.
     */
    public static String computeHash(String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileBytes);

            // Convert bytes to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            System.err.println("Failed to compute hash for " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Computes hash from a string directly.
     * Used when we want to hash the diff content rather than the full file.
     */
    public static String computeHashFromString(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            System.err.println("Hash computation failed: " + e.getMessage());
            return null;
        }
    }
}
