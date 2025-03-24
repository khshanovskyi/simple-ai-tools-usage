package task.dto;

import lombok.Getter;

@Getter
public enum Model {
    GPT_4o_SEARCH("gpt-4o-search-preview"),
    GPT_35_TURBO("gpt-3.5-turbo"),
    GPT_4o_MINI("gpt-4o-mini"),
    GPT_4o("gpt-4o");

    private final String value;

    Model(String value) {
        this.value = value;
    }

}
