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

/**
 * Performs search in WEB by request
 */
public class WebSearchTool implements BaseTool {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String openAiApiKey;

    public WebSearchTool(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        this.httpClient = HttpClient.newBuilder().build();
        this.mapper = new ObjectMapper();
    }

    @Override
    public String execute(Map<String, Object> arguments) {
        try {
            String request = String.valueOf(arguments.get("request"));
            return search(request);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @SneakyThrows
    private String search(String request) {
        //todo: 1. Create a Map for the request body with the following structure:
        //todo:    - "model": Use Model.GPT_4o_SEARCH.getValue() from your Model enum
        //todo:    - "web_search_options": Include an empty Map (Map.of())
        //todo:    - "messages": Create a List containing one Map with:
        //todo:      - role: "user"
        //todo:      - content: The request parameter passed to this method
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
        //todo: 6. If no error, extract the search result content from the response:
        //todo:    - Navigate to jsonNode.get("choices").get(0).get("message").get("content")
        //todo:    - Return this text value using asText()

        throw new RuntimeException("Not implemented");
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
