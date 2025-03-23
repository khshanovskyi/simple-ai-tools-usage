package task.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import task.dto.Model;
import task.utils.Constant;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class WebSearchTool {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String openAiApiKey;

    public WebSearchTool(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    public String search(JsonNode jsonNode) {
        String query = jsonNode.get("request").asText();

        return searchInWeb(query);
    }

    @SneakyThrows
    private String searchInWeb(String request) {
        Map<String, Object> requestBody = Map.of(
                "model", Model.GPT_4o_SEARCH.getValue(),
                "web_search_options", Map.of(),
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", request
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


    //"web_search_options": {
//            "user_location": {
//                "type": "approximate",
//                "approximate": {
//                    "country": "GB",
//                    "city": "London",
//                    "region": "London",
//                }
//            }
//        },

}
