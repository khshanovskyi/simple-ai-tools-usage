package task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import task.dto.Message;
import task.dto.Model;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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
        //todo: 7. Get first choice as JsonNode (choices[0]) as choice JsonNode
        //todo: 8. Get `message` as JsonNode
        //todo: 9.1. If tool_calls are present (`finish_reason` is `tool_calls`):
        //todo:      - get JsonNode `tool_calls`
        //todo:      - add AI message to history
        //todo:      - call processToolCalls(List<Message> messages, JsonNode toolCalls)
        //todo:      - then call recursively `responseWithMessage(List<Message> messages)` (it will provide final response)
        //todo: 9.2. Otherwise return Message with AI response (feel free to use builder)

        throw new RuntimeException("Not implemented");
    }

    private ObjectNode createRequest(List<Message> messages) {
        //todo: 1. Create ObjectNode request (mapper.createObjectNode())
        //todo: 2. Add `model` to request
        //todo: 3. Add `messages` array (mapper.valueToTree(messages))
        //todo: 4. If `this.tools` are present ->  add `tools` to request (mapper.valueToTree(tools))
        //todo: 5. Return request

        throw new RuntimeException("Not implemented");
    }

    private HttpRequest generateRequest(ObjectNode requestBody) throws JsonProcessingException {
        //todo: Use HttpRequest builder to create http request:
        //todo: - Add token
        //todo: - Add content type
        //todo: - Make POST request with `requestBody` as string

        throw new RuntimeException("Not implemented");
    }

    private void processToolCalls(List<Message> messages, JsonNode toolCalls) throws JsonProcessingException {
        //todo: 1. Iterate through `toolCalls` and add to messages list AI responses with tools calls. It is important
        //todo:    to LLM for preserving and comprehending context.
        //todo: 2. Iterate through `toolCalls`
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

        throw new RuntimeException("Not implemented");
    }

    private String executeTool(String functionName, JsonNode arguments) {
        //todo: Here we will hardcode the switch that will execute tools
        //todo: Use switch on the functionName:
        //todo: Case `simple_calculator` -> MathTool.calculateExpression(arguments) (already implemented)
        //todo: Case `nasa_image_stealer` -> ImageStealerTool.getLargestMarsImageDescription(arguments) (already implemented)
        //todo: Case `haiku_generation_tool` -> HaikuGeneratorTool.generate(arguments) (need to implemented)
        //todo: Case `web_search_tool` -> WebSearchTool.search(arguments) (need to implemented)
        //todo: default case -> throw IllegalArgumentEx with state that `Unknown function` + functionName
        //todo: Use Constants from Constant class instead of magic strings

        throw new RuntimeException("Not implemented");
    }

}