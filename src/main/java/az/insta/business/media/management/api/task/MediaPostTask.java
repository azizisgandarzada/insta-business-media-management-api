package az.insta.business.media.management.api.task;

import static az.insta.business.media.management.api.enumeration.MediaType.IMAGE;
import static az.insta.business.media.management.api.enumeration.MediaType.VIDEO;

import az.insta.business.media.management.api.client.InstagramGraphClient;
import az.insta.business.media.management.api.dto.PostMediaResponse;
import az.insta.business.media.management.api.entity.Media;
import az.insta.business.media.management.api.enumeration.MediaProductType;
import az.insta.business.media.management.api.enumeration.MediaType;
import az.insta.business.media.management.api.enumeration.Status;
import az.insta.business.media.management.api.repository.MediaRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaPostTask {

    private final InstagramGraphClient instagramGraphClient;
    private final MediaRepository mediaRepository;

    @Scheduled(cron = "*/5 * 10-23 * * *", zone = "Asia/Baku")
    public void run() {
        mediaRepository.findAllByStatus(Status.APPROVED)
                .forEach(media -> {
                    if (media.getProductType() == MediaProductType.FEED) {
                        postFeedMedia(media);
                    } else if (media.getProductType() == MediaProductType.REELS) {
                        postReelsMedia(media);
                    }
                });
    }

    private void postFeedMedia(Media media) {
        if (media.getType() == IMAGE) {
            postImageMedia(media);
        } else if (media.getType() == VIDEO) {
            postVideoMedia(media);
        } else if (media.getType() == MediaType.CAROUSEL_ALBUM) {
            postCarouselMedia(media);
        }
    }

    private void postVideoMedia(Media media) {
        PostMediaResponse response = instagramGraphClient.postVideoMedia(media.getCaption(), media.getUrl(), false);
        handleResponse(response, media);
        mediaRepository.save(media);
    }

    private void postImageMedia(Media media) {
        PostMediaResponse response = instagramGraphClient.postImageMedia(media.getCaption(), media.getUrl(), false);
        handleResponse(response, media);
        mediaRepository.save(media);
    }

    private void postCarouselMedia(Media media) {
        List<Media> children = mediaRepository.findAllByParentOrderById(media);
        List<String> carouselMediaIds = children.stream()
                .map(childMedia -> {
                    PostMediaResponse childResponse;
                    if (childMedia.getType() == IMAGE) {
                        childResponse = instagramGraphClient.postImageMedia(media.getCaption(), childMedia.getUrl(), true);
                    } else {
                        childResponse = instagramGraphClient.postVideoMedia(media.getCaption(), childMedia.getUrl(), true);
                    }
                    return childResponse == null ? null : childResponse.id();
                })
                .filter(Objects::nonNull)
                .toList();
        PostMediaResponse response = instagramGraphClient.postCarouselMedia(media.getCaption(), carouselMediaIds);
        handleResponse(response, media);
        mediaRepository.save(media);
    }

    private void postReelsMedia(Media media) {
        PostMediaResponse response = instagramGraphClient.postReelsMedia(media.getCaption(), media.getUrl());
        handleResponse(response, media);
        mediaRepository.save(media);
    }

    private void handleResponse(PostMediaResponse response, Media media) {
        if (response == null) {
            media.setStatus(Status.FAILED);
            log.info("Failed to post media {id:{}, type:{}}", media.getId(), media.getType());
        } else {
            media.setCreationId(response.id());
            media.setStatus(Status.POSTING);
            log.info("Media is posting {id:{}, type:{}}", media.getId(), media.getType());
        }
    }

}
