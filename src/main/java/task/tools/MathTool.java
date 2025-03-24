package task.tools;

import lombok.Getter;

import java.util.Map;

public class MathTool implements BaseTool {

    @Override
    public String execute(Map<String, Object> arguments) {
        try {
            double num1 = getDoubleArgument(arguments, "num1");
            double num2 = getDoubleArgument(arguments, "num2");
            String operation = getStringArgument(arguments, "operation");

            MathOperation op = MathOperation.fromString(operation);
            return switch (op) {
                case ADD -> "Result: " + (num1 + num2);
                case SUBTRACT -> "Result: " + (num1 - num2);
                case MULTIPLY -> "Result: " + (num1 * num2);
                case DIVIDE -> num2 != 0 ? "Result: " + (num1 / num2) : "Error: Division by zero";
            };
        } catch (Exception e) {
            return "Error processing calculation. " + e.getMessage();
        }
    }

    private double getDoubleArgument(Map<String, Object> arguments, String key) {
        Object value = arguments.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value for " + key + ": " + value);
            }
        }
        throw new IllegalArgumentException("Missing or invalid value for " + key);
    }

    private String getStringArgument(Map<String, Object> arguments, String key) {
        Object value = arguments.get(key);
        return value != null ? value.toString() : "";
    }

    @Getter
    enum MathOperation {
        ADD("add"),
        SUBTRACT("subtract"),
        MULTIPLY("multiply"),
        DIVIDE("divide");

        private final String operation;

        MathOperation(String operation) {
            this.operation = operation;
        }

        public static MathOperation fromString(String operation) {
            for (MathOperation op : MathOperation.values()) {
                if (op.operation.equalsIgnoreCase(operation)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}
