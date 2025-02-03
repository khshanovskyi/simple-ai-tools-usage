package task.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import task.dto.Model;
import task.tools.dto.Image;
import task.tools.dto.ImageList;
import task.utils.Constant;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ImageStealerTool {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final String openAiApiKey;

    public ImageStealerTool(String openAiApiKey) {
        this.openAiApiKey = openAiApiKey;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.mapper = new ObjectMapper();
    }

    @SneakyThrows
    public String getLargestMarsImageDescription(JsonNode arguments) {
        int sol = arguments.get("sol").asInt();
        ImageList imageList = fetchImageList(sol);
        try {
            String largestPictureURL = getLargestPicture(imageList);
            return getImageDescription(largestPictureURL);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private ImageList fetchImageList(int sol) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(collectURI(sol))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), ImageList.class);
    }

    private URI collectURI(int sol) {
        if (Constant.NASA_API_KEY.isBlank()) throw new RuntimeException("Nasa API Key is blank");
        return URI.create(String.format("%s?sol=%d&api_key=%s",
                Constant.NASA_API,
                sol,
                Constant.NASA_API_KEY));
    }

    private String getLargestPicture(ImageList imageList) {
        return imageList.getPhotos().parallelStream()
                .peek(this::getContentLength)
                .max(Comparator.comparing(Image::getContentLength))
                .map(Image::getImageSource)
                .orElseThrow(() -> new RuntimeException("No images found"));
    }

    private void getContentLength(Image image) {
        try {
            HttpRequest headRequest = HttpRequest.newBuilder()
                    .uri(URI.create(image.getImageSource()))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<Void> headResponse = httpClient.send(headRequest, HttpResponse.BodyHandlers.discarding());

            long contentLength = headResponse.headers()
                    .firstValue("Content-Length")
                    .map(Long::parseLong)
                    .orElse(0L);

            image.setContentLength(contentLength);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process image: " + image.getImageSource(), e);
        }
    }

    @SneakyThrows
    private String getImageDescription(String url) {
        Map<String, Object> requestBody = Map.of(
                "model", Model.GPT_4o_MINI.getValue(),
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of(
                                                "type", "text",
                                                "text", "What is in this image?"
                                        ),
                                        Map.of(
                                                "type", "image_url",
                                                "image_url", Map.of(
                                                        "url", url
                                                )
                                        )
                                )
                        )
                )
        );

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(Constant.OPEN_AI_API_URI)
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        String responseBody = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        JsonNode jsonNode = mapper.readTree(responseBody);
        if (jsonNode.has("error")) {
            return jsonNode.get("error").asText();
        }
        return jsonNode.get("choices").get(0).get("message").get("content").asText();
    }

}