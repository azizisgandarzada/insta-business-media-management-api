package az.insta.business.media.management.api.repository;

import az.insta.business.media.management.api.entity.Media;
import az.insta.business.media.management.api.enumeration.Status;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MediaRepository extends JpaRepository<Media, Integer> {

    @EntityGraph(attributePaths = {"account", "parent"})
    List<Media> findAllByParentIsNullAndStatus(Status status);

    @EntityGraph(attributePaths = {"account", "parent"})
    List<Media> findAllByParent(Media parent);

    @Modifying
    @Transactional
    @Query("update Media m set m.status=:targetStatus where m.parent=:parent and m.status=:currentStatus")
    void updateByParentAndStatus(Media parent, Status currentStatus, Status targetStatus);

}
