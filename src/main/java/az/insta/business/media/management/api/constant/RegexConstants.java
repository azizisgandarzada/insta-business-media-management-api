package az.insta.business.media.management.api.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegexConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Command {

        public static final String APPROVE = "approve:\\d+";
        public static final String REJECT = "reject:\\d+";

    }

}
