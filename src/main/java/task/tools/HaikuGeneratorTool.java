package task.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import task.dto.Model;
import task.utils.Constant;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class HaikuGeneratorTool implements BaseTool {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String openAiApiKey;

    public HaikuGeneratorTool(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        this.httpClient = HttpClient.newBuilder().build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        try {
            String query = String.valueOf(arguments.get("query"));

            return generateHaiku(query);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @SneakyThrows
    private String generateHaiku(String query) {
        Map<String, Object> requestBody = Map.of(
                "model", Model.GPT_4o_MINI.getValue(),
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are expert in haiku creation. Your goal is to create haiku on user demand."
                        ),
                        Map.of(
                                "role", "user",
                                "content", query
                        )
                )
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(Constant.OPEN_AI_API_URI)
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        String responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode jsonNode = mapper.readTree(responseBody);
        if (jsonNode.has("error")) {
            return jsonNode.get("error").asText();
        }
        return jsonNode.get("choices").get(0).get("message").get("content").asText();
    }
}
