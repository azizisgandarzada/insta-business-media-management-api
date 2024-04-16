package az.insta.business.media.management.api.task;

import az.insta.business.media.management.api.enumeration.Status;
import az.insta.business.media.management.api.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaRetryTask {

    private final MediaRepository mediaRepository;

    @Scheduled(cron = "0 * 10-23 * * *", zone = "Asia/Baku")
    public void run() {
        mediaRepository.findAllByStatus(Status.FAILED)
                .forEach(media -> {
                    media.setStatus(Status.CREATED);
                    media.setCheckStatusAttempts(0);
                    mediaRepository.save(media);
                    log.info("Media moved to created status {id:{}, type:{}}", media.getId(), media.getType());
                });
    }

}
