package az.insta.business.media.management.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DiscoverBusinessResponse(
        String id,
        BusinessDiscovery businessDiscovery
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record BusinessDiscovery(
            Media media,
            String id
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Media(
            List<DataItem> data,
            Paging paging
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record DataItem(
            String mediaType,
            String mediaProductType,
            String caption,
            String id,
            String mediaUrl,
            String username,
            OffsetDateTime timestamp,
            Children children
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Children(
            List<DataItem> data
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Cursors(
            String after
    ) {

    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Paging(
            Cursors cursors
    ) {

    }

}
