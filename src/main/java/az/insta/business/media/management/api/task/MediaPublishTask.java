package az.insta.business.media.management.api.task;

import az.insta.business.media.management.api.client.InstagramGraphClient;
import az.insta.business.media.management.api.dto.PublishMediaResponse;
import az.insta.business.media.management.api.enumeration.MediaType;
import az.insta.business.media.management.api.enumeration.Status;
import az.insta.business.media.management.api.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaPublishTask {

    private final InstagramGraphClient instagramGraphClient;
    private final MediaRepository mediaRepository;

    @Scheduled(cron = "*/5 * 10-23 * * *", zone = "Asia/Baku")
    public void run() {
        mediaRepository.findAllByParentIsNullAndStatus(Status.POSTED)
                .forEach(media -> {
                    log.info("Media publishing {id:{}, type:{}}", media.getId(), media.getType());
                    PublishMediaResponse response = instagramGraphClient.publishMedia(media.getCreationId());
                    if (response == null) {
                        media.setStatus(Status.PUBLISH_FAILED);
                        if (media.getType().equals(MediaType.CAROUSEL_ALBUM.name())) {
                            mediaRepository.updateByParentAndStatus(media, Status.POSTED, Status.PUBLISH_FAILED);
                        }
                        log.info("Failed to publish media {id:{}, type:{}}", media.getId(), media.getType());
                    } else {
                        media.setStatus(Status.PUBLISHED);
                        if (media.getType().equals(MediaType.CAROUSEL_ALBUM.name())) {
                            mediaRepository.updateByParentAndStatus(media, Status.POSTED, Status.PUBLISHED);
                        }
                        log.info("Media published {id:{}, type:{}}", media.getId(), media.getType());
                    }
                    mediaRepository.save(media);
                });
    }

}
