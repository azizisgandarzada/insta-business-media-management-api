package az.insta.business.media.management.api.entity;

import az.insta.business.media.management.api.enumeration.MediaProductType;
import az.insta.business.media.management.api.enumeration.MediaType;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "media", indexes = {
        @Index(columnList = "status", name = "status_idx"),
        @Index(columnList = "checkStatusAttempts", name = "check_status_attempts_idx"),
        @Index(columnList = "status, updatedAt DESC", name = "status_updated_at_idx")
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

    @Enumerated(EnumType.STRING)
    private MediaType type;

    @Enumerated(EnumType.STRING)
    private MediaProductType productType;

    private OffsetDateTime timestamp;
    private String creationId;
    private int checkStatusAttempts;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Media parent;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

}
