package task.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Function {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String name;
    private final String arguments;

    @JsonCreator
    public Function(
            @JsonProperty("name") String name,
            @JsonProperty("arguments") String arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @JsonProperty("name")
    public String name() {
        return name;
    }

    @JsonProperty("arguments")
    public String getArguments() {
        return arguments;
    }

    // This method is not used for serialization, but for internal use
    public Map<String, Object> arguments() {
        try {
            return mapper.readValue(arguments, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse function arguments: " + e.getMessage(), e);
        }
    }
}