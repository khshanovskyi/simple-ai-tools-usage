package task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import task.dto.Message;
import task.dto.Model;
import task.dto.Role;
import task.tools.ImageStealerTool;
import task.tools.MathTool;
import task.utils.Constant;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class OpenAIClient {
    private final ObjectMapper mapper;
    private final HttpClient httpClient;
    private final Model model;
    private final String apiKey;
    private final List<Map<String, Object>> tools;

    public OpenAIClient(Model model, String apiKey, List<Map<String, Object>> tools) {
        this.model = model;
        this.apiKey = checkApiKey(apiKey);
        this.tools = tools;
        this.mapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    private String checkApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("apiKey cannot be null or empty");
        }
        return apiKey;
    }

    public Message responseWithMessage(List<Message> messages) throws Exception {
        ObjectNode request = createRequest(messages);
        System.out.println("REQUEST: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
        HttpRequest httpRequest = generateRequest(request);

        String responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode responseJson = mapper.readTree(responseBody);
        System.out.println("RESPONSE: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseJson));
        JsonNode choice = responseJson.get("choices").get(0);
        JsonNode message = choice.get("message");

        if (choice.get("finish_reason").asText().equals("tool_calls")) {
            JsonNode toolCalls = message.get("tool_calls");
            if (toolCalls != null) {
                processToolCalls(messages, toolCalls);
                return responseWithMessage(messages);
            }
        }
        return Message.builder()
                    .role(Role.AI)
                    .content(message.get("content").asText())
                    .build();
    }

    private ObjectNode createRequest(List<Message> messages) {
        ObjectNode request = mapper.createObjectNode();
        request.put("model", this.model.getValue());
        ArrayNode messageArray = mapper.valueToTree(messages);
        request.set("messages", messageArray);

        if (tools != null && !tools.isEmpty()) {
            ArrayNode toolsArray = mapper.valueToTree(tools);
            request.set("tools", toolsArray);
        }

        return request;
    }

    private HttpRequest generateRequest(ObjectNode requestBody) throws JsonProcessingException {
        return HttpRequest.newBuilder()
                .uri(Constant.OPEN_AI_API_URI)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();
    }

    private void processToolCalls(List<Message> messages, JsonNode toolCalls) throws JsonProcessingException {
        for (JsonNode toolCall : toolCalls) {
            String id = toolCall.get("id").asText();
            JsonNode functionNode = toolCall.get("function");
            String functionName = functionNode.get("name").asText();
            JsonNode arguments = mapper.readTree(functionNode.get("arguments").asText());

            String result = executeTool(functionName, arguments);

            messages.add(
                    Message.builder()
                    .role(Role.FUNCTION)
                    .name(functionName)
                    .toolCallId(id)
                    .content(result)
                    .build()
            );

            System.out.println("FUNCTION '" + functionName + "': " + result);
        }
    }

    private String executeTool(String functionName, JsonNode arguments) {
        return switch (functionName) {
            case Constant.SIMPLE_CALCULATOR -> MathTool.calculateExpression(arguments);
            case Constant.NASA_IMG_STEALER -> new ImageStealerTool(apiKey).getLargestMarsImageDescription(arguments);
            default -> throw new IllegalArgumentException("Unknown function: " + functionName);
        };
    }

}