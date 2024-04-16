package az.insta.business.media.management.api.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaStatusCode {

    EXPIRED(Status.FAILED),
    ERROR(Status.FAILED),
    FINISHED(Status.POSTED),
    IN_PROGRESS(Status.PUBLISHING),
    PUBLISHED(Status.PUBLISHED);

    private final Status dbStatus;

}
