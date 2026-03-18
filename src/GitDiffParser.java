import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitDiffParser {

    /**
     * Runs `git diff` and extracts the list of modified Java files.
     * Only returns .java files to avoid processing non-code changes.
     */
    public static List<String> getChangedFiles(String repoPath) {
        List<String> changedFiles = new ArrayList<>();

        try {
            ProcessBuilder pb = new ProcessBuilder("git", "diff", "--name-only", "HEAD");
            pb.directory(new java.io.File(repoPath));
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                // Only process Java source files
                if (line.endsWith(".java")) {
                    changedFiles.add(repoPath + "/" + line);
                }
            }

            process.waitFor();

        } catch (Exception e) {
            System.err.println("Failed to run git diff: " + e.getMessage());
        }

        return changedFiles;
    }

    /**
     * Returns the raw diff content for a specific file.
     * Used to give the LLM context about what exactly changed.
     */
    public static String getDiffContent(String repoPath, String filePath) {
        StringBuilder diffContent = new StringBuilder();

        try {
            ProcessBuilder pb = new ProcessBuilder("git", "diff", "HEAD", "--", filePath);
            pb.directory(new java.io.File(repoPath));
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                diffContent.append(line).append("\n");
            }

            process.waitFor();

        } catch (Exception e) {
            System.err.println("Failed to get diff content: " + e.getMessage());
        }

        return diffContent.toString();
    }
}
