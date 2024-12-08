package itmo.labs.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private ImportStatus status = ImportStatus.PENDING;

    private String performedBy;

    @Column(length = 1024)
    private String fileUrl;

    @Column(length = 1024)
    private int recordsImported;

    public enum ImportStatus {
        SUCCESS,
        PENDING,
        FAILURE
    }
    private String errorMessage = null;
}