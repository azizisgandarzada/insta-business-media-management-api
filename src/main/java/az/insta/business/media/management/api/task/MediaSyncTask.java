package az.insta.business.media.management.api.task;

import static az.insta.business.media.management.api.enumeration.MediaProductType.FEED;
import static az.insta.business.media.management.api.enumeration.MediaProductType.REELS;

import az.insta.business.media.management.api.client.InstagramGraphClient;
import az.insta.business.media.management.api.dto.DiscoverBusinessResponse;
import az.insta.business.media.management.api.entity.Account;
import az.insta.business.media.management.api.entity.Media;
import az.insta.business.media.management.api.enumeration.MediaProductType;
import az.insta.business.media.management.api.enumeration.Status;
import az.insta.business.media.management.api.repository.AccountRepository;
import az.insta.business.media.management.api.repository.MediaRepository;
import az.insta.business.media.management.api.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaSyncTask {

    private final InstagramGraphClient instagramGraphClient;
    private final AccountRepository accountRepository;
    private final MediaRepository mediaRepository;

    @Value("${media.caption.template}")
    private String captionTemplate;

    @Scheduled(cron = "0 0 10-23 * * *", zone = "Asia/Baku")
    public void run() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            log.info("New media syncing {username:{}}", account.getUsername());
            DiscoverBusinessResponse response = instagramGraphClient.discoverBusiness(account.getUsername());
            if (response == null || response.businessDiscovery().media() == null) {
                log.warn("Business discovery not found {username:{}}", account.getUsername());
                continue;
            }
            var mediaList = new ArrayList<Media>();
            var childMediaList = new ArrayList<Media>();
            for (DiscoverBusinessResponse.DataItem data : response.businessDiscovery().media().data()) {
                if (isViolatedCopyright(data)) {
                    continue;
                }
                if (isUnsupportedMediaProductType(data)) {
                    continue;
                }
                if (account.getLastMediaTimestamp() != null
                        && !data.timestamp().isAfter(account.getLastMediaTimestamp())) {
                    continue;
                }
                Media media = buildMedia(data);
                media.setAccount(account);
                if (data.children() != null) {
                    for (DiscoverBusinessResponse.DataItem childData : data.children().data()) {
                        var childMedia = buildMedia(childData);
                        childMedia.setAccount(account);
                        childMedia.setParent(media);
                        childMediaList.add(childMedia);
                    }
                }
                mediaList.add(media);
            }
            if (mediaList.isEmpty()) {
                log.info("Media is up-to-date {username:{}}", account.getUsername());
            } else {
                int mediaCount = mediaList.size();
                int childMediaCount = childMediaList.size();
                mediaList.addAll(childMediaList);
                account.setLastMediaTimestamp(response.businessDiscovery().media().data().get(0).timestamp());
                mediaRepository.saveAll(mediaList);
                accountRepository.save(account);
                log.info("New media synced {username:{}, mediaCount:{}, childMediaCount:{}}", account.getUsername(),
                        mediaCount, childMediaCount);
            }
        }
    }

    private Media buildMedia(DiscoverBusinessResponse.DataItem dataItem) {
        var media = new Media();
        media.setIgId(dataItem.id());
        media.setType(dataItem.mediaType());
        media.setProductType(dataItem.mediaProductType());
        media.setUrl(dataItem.mediaUrl());
        media.setStatus(Status.CREATED);
        media.setTimestamp(dataItem.timestamp());
        String caption = captionTemplate.replace(":username", dataItem.username());
        if (StringUtils.isNotBlank(dataItem.caption())) {
            caption = caption.replace(":text", dataItem.caption());
        }
        media.setCaption(caption);
        return media;
    }

    private boolean isViolatedCopyright(DiscoverBusinessResponse.DataItem data) {
        return StringUtils.isBlank(data.mediaUrl());
    }

    private boolean isUnsupportedMediaProductType(DiscoverBusinessResponse.DataItem data) {
        var mediaProductType = MediaProductType.of(data.mediaProductType());
        return mediaProductType != FEED && mediaProductType != REELS;
    }

}
