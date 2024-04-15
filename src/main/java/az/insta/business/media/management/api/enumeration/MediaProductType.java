package az.insta.business.media.management.api.enumeration;

import java.util.Arrays;

public enum MediaProductType {

    AD,
    FEED,
    STORY,
    REELS;

    public static MediaProductType of(String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(value -> value.name().equals(name))
                .findFirst()
                .orElse(null);
    }


}
