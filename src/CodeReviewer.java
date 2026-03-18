public class CodeReviewer {

    // Entry point: run review on current git diff
    public void runReview(String repoPath) {
        List<String> changedFiles = GitDiffParser.getChangedFiles(repoPath);
        
        for (String file : changedFiles) {
            String hash = HashUtil.computeHash(file);
            
            // Check cache first
            if (cache.contains(hash)) {
                printReview(cache.get(hash));
                continue;
            }
            
            // Send to LLM if not cached
            String fileContent = FileUtil.readFile(file);
            ReviewResult result = LLMClient.analyze(fileContent);
            
            cache.put(hash, result);
            printReview(result);
        }
    }

    // Structure output by severity
    private void printReview(ReviewResult result) {
        System.out.println("=== BUGS ===");
        result.getBugs().forEach(System.out::println);
        
        System.out.println("=== PERFORMANCE ===");
        result.getPerformanceIssues().forEach(System.out::println);
        
        System.out.println("=== STYLE ===");
        result.getStyleIssues().forEach(System.out::println);
    }
}
