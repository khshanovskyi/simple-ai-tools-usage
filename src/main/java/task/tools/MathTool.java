package task.tools;

import com.fasterxml.jackson.databind.JsonNode;

public class MathTool {

    public static String calculateExpression(JsonNode arguments) {
        try {
            double num1 = arguments.get("num1").asDouble();
            double num2 = arguments.get("num2").asDouble();
            String operation = arguments.get("operation").asText();

            MathOperation op = MathOperation.fromString(operation);
            return switch (op) {
                case ADD -> "Result: " + (num1 + num2);
                case SUBTRACT -> "Result: " + (num1 - num2);
                case MULTIPLY -> "Result: " + (num1 * num2);
                case DIVIDE -> num2 != 0 ? "Result: " + (num1 / num2) : "Error: Division by zero";
            };
        } catch (Exception e) {
            return "Error processing calculation.";
        }
    }
}
