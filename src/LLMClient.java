import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LLMClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4";

    private final String apiKey;
    private final HttpClient httpClient;

    public LLMClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Sends file content to OpenAI API and returns structured review feedback.
     * Prompt is designed to return consistent JSON output for easy parsing.
     */
    public ReviewResult analyze(String fileContent) {
        String prompt = buildPrompt(fileContent);
        String requestBody = buildRequestBody(prompt);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            return parseResponse(response.body());

        } catch (Exception e) {
            System.err.println("LLM API call failed: " + e.getMessage());
            return ReviewResult.empty();
        }
    }

    private String buildPrompt(String fileContent) {
        return "Review the following Java code. " +
               "Return a JSON object with three arrays: 'bugs', 'performance', 'style'. " +
               "Each item should have 'line' and 'description' fields. " +
               "Be specific and actionable.\n\n" +
               "Code:\n" + fileContent;
    }

    private String buildRequestBody(String prompt) {
        return String.format(
            "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
            MODEL,
            prompt.replace("\"", "\\\"").replace("\n", "\\n")
        );
    }

    private ReviewResult parseResponse(String responseBody) {
        // Parse JSON response and map to ReviewResult
        // In production: use a JSON library like Jackson or Gson
        return ReviewResult.fromJson(responseBody);
    }
}
