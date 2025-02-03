package task.tools;

public enum MathOperation {
    ADD("add"),
    SUBTRACT("subtract"),
    MULTIPLY("multiply"),
    DIVIDE("divide");

    private final String operation;

    MathOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
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
