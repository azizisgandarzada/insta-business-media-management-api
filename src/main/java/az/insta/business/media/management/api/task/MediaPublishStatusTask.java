package az.insta.business.media.management.api.task;

import az.insta.business.media.management.api.client.InstagramGraphClient;
import az.insta.business.media.management.api.dto.CheckStatusResponse;
import az.insta.business.media.management.api.enumeration.MediaStatusCode;
import az.insta.business.media.management.api.enumeration.Status;
import az.insta.business.media.management.api.repository.MediaRepository;
import java.time.Duration;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaPublishStatusTask {

    private final InstagramGraphClient instagramGraphClient;
    private final MediaRepository mediaRepository;

    @Value("${client.instagram-graph.metadata.check-status-attempts}")
    private Integer attempts;

    @Value("${client.instagram-graph.metadata.check-status-interval}")
    private Duration interval;

    @Scheduled(cron = "*/2 * 10-23 * * *", zone = "Asia/Baku")
    public void run() {
        var updatedAt = OffsetDateTime.now().minusSeconds(interval.getSeconds());
        mediaRepository.findAllByStatusAndCheckStatusAttemptsAndUpdatedAt(Status.PUBLISHING, attempts, updatedAt)
                .forEach(media -> {
                    CheckStatusResponse response = instagramGraphClient.checkStatus(media.getCreationId());
                    if (response != null) {
                        media.setStatus(response.statusCode().getDbStatus());
                    }
                    MediaStatusCode status = response == null ? null : response.statusCode();
                    log.info("Checking publishing status {id:{}, type:{}, status:{}}", media.getId(), media.getType(), status);
                    media.setCheckStatusAttempts(media.getCheckStatusAttempts() + 1);
                    mediaRepository.save(media);
                });
    }

}
