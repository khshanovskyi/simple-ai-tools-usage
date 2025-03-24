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
        var request = createRequest(messages);
        System.out.println("REQUEST: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
        HttpRequest httpRequest = generateRequest(request);

        String responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        System.out.println("RESPONSE: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readTree(responseBody)));

        ChatCompletion chatCompletion = mapper.readValue(responseBody, ChatCompletion.class);
        Choice choice = chatCompletion.choices().getFirst();
        Message message = choice.message();

        if (choice.finishReason().equals("tool_calls")) {
            List<ToolCall> toolCalls = message.getToolCalls();
            if (toolCalls != null && !toolCalls.isEmpty()) {
                //Add AI message
                messages.add(message);

                processToolCalls(messages, toolCalls);
                return responseWithMessage(messages);
            }
        }

        return Message.builder()
                .role(Role.AI)
                .content(message.getContent())
                .build();
    }

    private Map<String, Object> createRequest(List<Message> messages) {
        return Map.of(
                "model", this.model.getValue(),
                "messages", messages,
                "tools", this.tools
        );
    }

    private HttpRequest generateRequest(Map<String, Object> requestBody) throws JsonProcessingException {
        return HttpRequest.newBuilder()
                .uri(Constant.OPEN_AI_API_URI)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();
    }

    private void processToolCalls(List<Message> messages, List<ToolCall> toolCalls) {
        for (ToolCall toolCall : toolCalls) {

            String id = toolCall.id();
            Function function = toolCall.function();

            String functionName = function.name();
            String result = executeTool(functionName, function.arguments());

            messages.add(
                    Message.builder()
                            .role(Role.TOOL)
                            .name(functionName)
                            .toolCallId(id)
                            .content(result)
                            .build()
            );

            System.out.println("FUNCTION '" + functionName + "': " + result);
        }
    }

    private String executeTool(String functionName, Map<String, Object> arguments) {
        return switch (functionName) {
            case Constant.SIMPLE_CALCULATOR -> new MathTool().execute(arguments);
            case Constant.NASA_IMG_STEALER -> new ImageStealerTool(apiKey).execute(arguments);
            case Constant.HAIKU_GENERATOR -> new HaikuGeneratorTool(apiKey).execute(arguments);
            case Constant.WEB_SEARCH -> new WebSearchTool(apiKey).execute(arguments);
            default -> "Unknown function: " + functionName;
        };
    }
}