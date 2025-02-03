package task.dto;

import java.util.Map;

public record Function(String name, Map<String, Object> arguments) {
}
