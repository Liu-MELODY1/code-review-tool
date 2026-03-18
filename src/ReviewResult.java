import java.util.List;
import java.util.ArrayList;

public class ReviewResult {

    private List<Issue> bugs;
    private List<Issue> performanceIssues;
    private List<Issue> styleIssues;

    public ReviewResult() {
        this.bugs = new ArrayList<>();
        this.performanceIssues = new ArrayList<>();
        this.styleIssues = new ArrayList<>();
    }

    /**
     * Represents a single review issue with location and description.
     */
    public static class Issue {
        private int lineNumber;
        private String description;
        private Severity severity;

        public enum Severity {
            HIGH, MEDIUM, LOW
        }

        public Issue(int lineNumber, String description, Severity severity) {
            this.lineNumber = lineNumber;
            this.description = description;
            this.severity = severity;
        }

        @Override
        public String toString() {
            return String.format("[%s] Line %d: %s", severity, lineNumber, description);
        }
    }

    public static ReviewResult empty() {
        return new ReviewResult();
    }

    /**
     * Parses LLM JSON response into structured ReviewResult.
     */
    public static ReviewResult fromJson(String json) {
        // Simplified parser - production would use Jackson/Gson
        ReviewResult result = new ReviewResult();
        // JSON parsing logic here
        return result;
    }

    public List<Issue> getBugs() { return bugs; }
    public List<Issue> getPerformanceIssues() { return performanceIssues; }
    public List<Issue> getStyleIssues() { return styleIssues; }

    public int getTotalIssues() {
        return bugs.size() + performanceIssues.size() + styleIssues.size();
    }
}
