package az.insta.business.media.management.api.util;

import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtils {

    public static boolean isEmpty(Collection<?> coll) {
        return org.springframework.util.CollectionUtils.isEmpty(coll);
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

}
