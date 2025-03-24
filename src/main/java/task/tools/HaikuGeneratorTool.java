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

public class HaikuGeneratorTool {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String openAiApiKey;

    public HaikuGeneratorTool(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        this.httpClient = HttpClient.newBuilder().build();
        this.mapper = new ObjectMapper();
    }


    public String generate(JsonNode jsonNode) {
        String query = jsonNode.get("query").asText();

        return generate(query);
    }

    @SneakyThrows
    private String generate(String query) {
        //todo: 1. Create a Map for the request body with the following structure:
        //todo:    - "model": Use Model.GPT_4o_MINI.getValue() from your Model enum
        //todo:    - "messages": Create a List of Maps containing:
        //todo:      a. System message with role "system" and content about being a haiku expert
        //todo:      b. User message with role "user" and the `query` parameter as content
        //todo: 2. Build an HttpRequest using HttpRequest.newBuilder()
        //todo:    - Set the URI to Constant.OPEN_AI_API_URI
        //todo:    - Add "Authorization" header with "Bearer " + openAiApiKey
        //todo:    - Add "Content-Type" header with value "application/json"
        //todo:    - Set POST method with the requestBody converted to string using mapper.writeValueAsString()
        //todo: 3. Send the HTTP request using httpClient.send()
        //todo:    - Use HttpResponse.BodyHandlers.ofString() to get response as String
        //todo:    - Store the response body
        //todo: 4. Parse the response body to JsonNode using mapper.readTree()
        //todo: 5. Check if the response contains an "error" field
        //todo:    - If yes, return the error message using jsonNode.get("error").asText()
        //todo: 6. If no error, extract the haiku content from the response:
        //todo:    - Navigate to jsonNode.get("choices").get(0).get("message").get("content")
        //todo:    - Return this text value using asText()

        throw new RuntimeException("Not implemented");
    }
}
