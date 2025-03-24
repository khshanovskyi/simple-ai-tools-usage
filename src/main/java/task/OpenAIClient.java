package task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import task.dto.ChatCompletion;
import task.dto.Choice;
import task.dto.Function;
import task.dto.Message;
import task.dto.Model;
import task.dto.Role;
import task.dto.ToolCall;
import task.tools.HaikuGeneratorTool;
import task.tools.ImageStealerTool;
import task.tools.MathTool;
import task.tools.WebSearchTool;
import task.utils.Constant;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OpenAIClient {
    private final ObjectMapper mapper;
    private final HttpClient httpClient;
    private final Model model;
    private final String apiKey;
    private final List<Map<String, Object>> tools;

    public OpenAIClient(Model model, String apiKey, List<Map<String, Object>> tools) {
        this.model = model;
        this.apiKey = checkApiKey(apiKey);
        this.tools = Objects.nonNull(tools) ? tools : new ArrayList<>();
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
        //todo: 5. Read response body to ChatCompletion (mapper.readValue(...))
        //todo: 6. (Optional) print generated response to console, it will help to see JSON response
        //todo:              you can use `mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response))`
        //todo:              or `mapper.writeValueAsString(response))`
        //todo: 7. Get first Choice from ChatCompletion
        //todo: 8. Get `message` from Choice
        //todo: 9.1. If tool_calls are present in Choice (`finish_reason` is `tool_calls`):
        //todo:      - get `tool_calls`
        //todo:      - add AI message to history
        //todo:      - call processToolCalls(List<Message> messages, List<ToolCall> toolCalls)
        //todo:      - then call recursively `responseWithMessage(List<Message> messages)` (it will provide final response)
        //todo: 9.2. Otherwise return Message with AI response

        throw new RuntimeException("Not implemented");
    }

    private Map<String, Object> createRequest(List<Message> messages) {
        //todo: Create map of such params
        //todo:     - `model`
        //todo:     - `messages`
        //todo:     - `tools`

        throw new RuntimeException("Not implemented");
    }

    private HttpRequest generateRequest(Map<String, Object> requestBody) throws JsonProcessingException {
        //todo: Use HttpRequest builder to create http request:
        //todo: - Add token
        //todo: - Add content type
        //todo: - Make POST request with `requestBody` as string

        throw new RuntimeException("Not implemented");
    }

    private void processToolCalls(List<Message> messages, List<ToolCall> toolCalls) {
        //todo: Iterate through `toolCalls`
        //todo: Get:
        //todo:     - tool call id
        //todo:     - function name
        //todo:     - function arguments
        //todo: Call `executeTool(String functionName, Map<String, Object> arguments)`. Pay attention that we have simplified
        //todo:       version of tool calls and extract args on the tool level from the `Map<String, Object>  arguments`
        //todo: Add message to messages list:
        //todo:     - role: `tool`
        //todo:     - tool_call_id: original tool call is (call_abc...)
        //todo:     - name: function name
        //todo:     - content: execution result
        //todo: (Optional) print generated response to console, it will help to see function result

        throw new RuntimeException("Not implemented");
    }

    private String executeTool(String functionName, Map<String, Object> arguments) {
        //todo: Here we will hardcode the switch that will execute tools
        //todo: Use switch on the functionName:
        //todo: Case `simple_calculator` -> new MathTool().execute(arguments) (already implemented)
        //todo: Case `nasa_image_stealer` -> new ImageStealerTool(apiKey).execute(arguments) (already implemented)
        //todo: Case `haiku_generation_tool` -> new HaikuGeneratorTool(apiKey).execute(arguments) (need to implemented)
        //todo: Case `web_search_tool` -> new WebSearchTool(apiKey).execute(arguments) (need to implemented)
        //todo: default case -> "`Unknown function` + functionName"
        //todo: Use Constants from Constant class instead of magic strings

        throw new RuntimeException("Not implemented");
    }
}