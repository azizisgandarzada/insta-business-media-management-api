package az.insta.business.media.management.api.dto;

import az.insta.business.media.management.api.enumeration.MediaStatusCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CheckStatusResponse(String id, MediaStatusCode statusCode) {

}
