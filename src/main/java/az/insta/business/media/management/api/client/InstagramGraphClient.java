package az.insta.business.media.management.api.client;

import az.insta.business.media.management.api.constant.SymbolConstants;
import az.insta.business.media.management.api.dto.CheckStatusResponse;
import az.insta.business.media.management.api.dto.DiscoverBusinessResponse;
import az.insta.business.media.management.api.dto.PostMediaResponse;
import az.insta.business.media.management.api.dto.PublishMediaResponse;
import az.insta.business.media.management.api.enumeration.MediaPostType;
import az.insta.business.media.management.api.util.CollectionUtils;
import az.insta.business.media.management.api.util.StringUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstagramGraphClient {

    @Qualifier("instagramGraphWebClient")
    private final WebClient webClient;

    @Value("${client.instagram-graph.credentials.access-token}")
    private String accessToken;

    @Value("${client.instagram-graph.metadata.business-id}")
    private String businessId;

    public DiscoverBusinessResponse discoverBusiness(String username) {
        return webClient.get()
                .uri(uri -> uri.path("/{businessId}")
                        .queryParam("fields", "business_discovery.username({username})" +
                                "{media.limit(5){id,caption,username,media_url,timestamp,media_type,media_product_type," +
                                "children{id,username,media_url,media_type}}}")
                        .queryParam("access_token", accessToken)
                        .build(businessId, username))
                .retrieve()
                .bodyToMono(DiscoverBusinessResponse.class)
                .doOnError(ex -> log.error("Failed to discover business {ex:{}}", ex.getMessage(), ex))
                .onErrorResume(ex -> Mono.empty())
                .block();
    }

    public PostMediaResponse postImageMedia(String caption,
                                            String imageUrl,
                                            Boolean isCarouselItem) {
        return postMedia(MediaPostType.IMAGE, caption, imageUrl, null, isCarouselItem, null);
    }

    public PostMediaResponse postVideoMedia(String caption,
                                            String videoUrl,
                                            Boolean isCarouselItem) {
        return postMedia(MediaPostType.VIDEO, caption, null, videoUrl, isCarouselItem, null);
    }

    public PostMediaResponse postCarouselMedia(String caption,
                                               List<String> carouselMediaIds) {
        return postMedia(MediaPostType.CAROUSEL, caption, null, null, null, carouselMediaIds);
    }

    public PostMediaResponse postReelsMedia(String caption, String videoUrl) {
        return postMedia(MediaPostType.REELS, caption, null, videoUrl, null, null);
    }

    public PostMediaResponse postMedia(MediaPostType type,
                                       String caption,
                                       String imageUrl,
                                       String videoUrl,
                                       Boolean isCarouselItem,
                                       List<String> carouselMediaIds) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (StringUtils.isNotBlank(caption)) {
            queryParams.add("caption", caption);
        }
        if (type != MediaPostType.IMAGE) {
            queryParams.add("media_type", type.name());
        }
        if (StringUtils.isNotBlank(imageUrl)) {
            queryParams.add("image_url", imageUrl);
        }
        if (StringUtils.isNotBlank(videoUrl)) {
            queryParams.add("video_url", videoUrl);
        }
        if (Boolean.TRUE.equals(isCarouselItem)) {
            queryParams.add("is_carousel_item", isCarouselItem.toString());
        }
        if (CollectionUtils.isNotEmpty(carouselMediaIds)) {
            queryParams.add("children", String.join(SymbolConstants.COMMA, carouselMediaIds));
        }
        queryParams.add("access_token", accessToken);
        return webClient.post()
                .uri(uri -> uri.path("/{businessId}/media")
                        .queryParams(queryParams)
                        .build(businessId))
                .retrieve()
                .bodyToMono(PostMediaResponse.class)
                .doOnError(ex -> log.error("Failed to post media {ex:{}}", ex.getMessage(), ex))
                .onErrorResume(ex -> Mono.empty())
                .block();
    }

    public CheckStatusResponse checkStatus(String creationId) {
        return webClient.get()
                .uri(uri -> uri.path("/{creationId}")
                        .queryParam("fields", "status_code")
                        .queryParam("access_token", accessToken)
                        .build(creationId))
                .retrieve()
                .bodyToMono(CheckStatusResponse.class)
                .doOnError(ex -> log.error("Failed to check status {ex:{}}", ex.getMessage(), ex))
                .onErrorResume(ex -> Mono.empty())
                .block();
    }

    public PublishMediaResponse publishMedia(String creationId) {
        return webClient.post()
                .uri(uri -> uri.path("/{businessId}/media_publish")
                        .queryParam("creation_id", creationId)
                        .queryParam("access_token", accessToken)
                        .build(businessId))
                .retrieve()
                .bodyToMono(PublishMediaResponse.class)
                .doOnError(ex -> log.error("Failed to publish media {ex:{}}", ex.getMessage(), ex))
                .onErrorResume(ex -> Mono.empty())
                .block();
    }

}
