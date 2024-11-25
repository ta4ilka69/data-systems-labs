package itmo.labs.dto;

import itmo.labs.model.ImportHistory;
import itmo.labs.model.ImportHistory.ImportStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImportHistoryUpdateDTO {
    private Long id;
    private LocalDateTime timestamp;
    private ImportStatus status;
    private String performedBy;
    private int recordsImported;
    private String errorMessage;
    public ImportHistoryUpdateDTO(ImportHistory importHistory) {
        this.id = importHistory.getId();
        this.timestamp = importHistory.getTimestamp();
        this.status = importHistory.getStatus();
        this.performedBy = importHistory.getPerformedBy();
        this.recordsImported = importHistory.getRecordsImported();
        this.errorMessage = importHistory.getErrorMessage();
    }
}