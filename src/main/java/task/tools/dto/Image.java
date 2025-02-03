package task.tools.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {
    int id;
    int sol;
    @JsonProperty("img_src")
    String imageSource;

    long contentLength;
    URI originUri;
}
