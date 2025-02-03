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
        //todo: 1. Create request, you need to implement method `createRequest(List<Message> messages)`
        //todo: 2. (Optional) print generated request to console, it will help to see JSON request
        //todo:              you can use `mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request))`
        //todo:              or `mapper.writeValueAsString(request))`
        //todo: 3. Generate HttpRequest, you need to implement method `generateRequest(ObjectNode request)`
        //todo: 4. Send request and get response body (to handle response body use `HttpResponse.BodyHandlers.ofString()`)
        //todo: 5. Read response body to Tree by ObjectMapper
        //todo: 6. (Optional) print generated response to console, it will help to see JSON response
        //todo:              you can use `mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response))`
        //todo:              or `mapper.writeValueAsString(response))`
        //todo: 7. Get `message` as JsonNode (choices[0]message)
        //todo: 8. Get `tool_calls` as JsonNode
        //todo: 9.1. If tool_calls are present, call processToolCalls(List<Message> messages, JsonNode toolCalls)
        //todo:      and then call recursively `responseWithMessage(List<Message> messages)` (it will provide final response)
        //todo: 9.2. Otherwise return Message with AI response (feel free to use builder)

        ObjectNode request = createRequest(messages);
        System.out.println("REQUEST: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
        HttpRequest httpRequest = generateRequest(request);

        String responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode responseJson = mapper.readTree(responseBody);
        System.out.println("RESPONSE: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseJson));
        JsonNode message = responseJson.get("choices").get(0).get("message");

        JsonNode toolCalls = message.get("tool_calls");
        if (toolCalls != null && toolCalls.isArray() && !toolCalls.isEmpty()) {
            processToolCalls(messages, toolCalls);
            return responseWithMessage(messages);
        } else {
            return Message.builder()
                    .role(Role.AI)
                    .content(message.get("content").asText())
                    .build();
        }
    }

    private ObjectNode createRequest(List<Message> messages) {
        //todo: 1. Create ObjectNode request (mapper.createObjectNode())
        //todo: 2. Add `model` to request
        //todo: 3. Add `messages` array (mapper.valueToTree(messages))
        //todo: 4. If `this.tools` are present ->  add `tools` to request (mapper.valueToTree(tools))
        //todo: 5. Return request

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
        //todo: Use HttpRequest builder to create http request:
        //todo: - Add token
        //todo: - Add content type
        //todo: - Make POST request with `requestBody` as string
        return HttpRequest.newBuilder()
                .uri(Constant.OPEN_AI_API_URI)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();
    }

    private void processToolCalls(List<Message> messages, JsonNode toolCalls) throws JsonProcessingException {
        //todo: Iterate through `toolCalls`
        //todo: Get:
        //todo:     - tool call id
        //todo:     - function name
        //todo:     - function arguments
        //todo: Call `executeTool(String functionName, JsonNode arguments)`. Pay attention that we have simplified
        //todo:       version of tool calls and extract args on the tool level from the `JsonNode arguments`
        //todo: Add message to messages list:
        //todo:     - role: function
        //todo:     - tool_call_id: tool call id
        //todo:     - name: function name
        //todo:     - content: execution result
        //todo: (Optional) print generated response to console, it will help to see function result

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
        //todo: Here we will hardcode the switch that will execute tools
        //todo: Use switch on the functionName:
        //todo: Case `simple_calculator` -> MathTool.calculateExpression(arguments)
        //todo: Case `nasa_image_stealer` -> MathTool.calculateExpression(arguments)
        //todo: default case -> throw IllegalArgumentEx with state that `Unknown function` + functionName
        //todo: Use Constants from Constant class instead of magic strings
        return switch (functionName) {
            case Constant.SIMPLE_CALCULATOR -> MathTool.calculateExpression(arguments);
            case Constant.NASA_IMG_STEALER -> new ImageStealerTool(apiKey).getLargestMarsImageDescription(arguments);
            default -> throw new IllegalArgumentException("Unknown function: " + functionName);
        };
    }

}