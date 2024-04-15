package az.insta.business.media.management.api.entity;

import az.insta.business.media.management.api.enumeration.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "media", indexes = {
        @Index(columnList = "status", name = "status_idx")
})
@Data
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String igId;

    @Column(length = 9999999)
    private String caption;

    @Column(length = 9999999)
    private String url;

    private String type;
    private String productType;
    private OffsetDateTime timestamp;
    private String creationId;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Media parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

}
