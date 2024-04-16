package az.insta.business.media.management.api.repository;

import az.insta.business.media.management.api.entity.Media;
import az.insta.business.media.management.api.enumeration.Status;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MediaRepository extends JpaRepository<Media, Integer> {

    @EntityGraph(attributePaths = {"account", "parent"})
    @Query("select m from Media m where m.parent is null and m.status=:status")
    List<Media> findAllByStatus(Status status);

    @EntityGraph(attributePaths = {"account", "parent"})
    @Query("select m from Media m where m.parent is null and m.status=:status and m.checkStatusAttempts<:attempts and" +
            " m.updatedAt<:updatedAt")
    List<Media> findAllByStatusAndCheckStatusAttemptsAndUpdatedAt(Status status,
                                                                  Integer attempts,
                                                                  OffsetDateTime updatedAt);

    @EntityGraph(attributePaths = {"account", "parent"})
    List<Media> findAllByParentOrderById(Media parent);

}
