package itmo.labs.controller;

import itmo.labs.dto.ImportHistoryUpdateDTO;
import itmo.labs.dto.RouteUpdateDTO;
import itmo.labs.model.ImportHistory;
import itmo.labs.model.OperationType;
import itmo.labs.model.ImportHistory.ImportStatus;
import itmo.labs.repository.ImportHistoryRepository;
import itmo.labs.service.RouteImportService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/routes")
public class RouteImportController {

    private final RouteImportService routeImportService;
    private final ImportHistoryRepository importHistoryRepository;
    private final RouteWebSocketController routeWebSocketController;

    @Autowired
    public RouteImportController(RouteImportService routeImportService, ImportHistoryRepository importHistoryRepository,
            RouteWebSocketController routeWebSocketController) {
        this.routeImportService = routeImportService;
        this.importHistoryRepository = importHistoryRepository;
        this.routeWebSocketController = routeWebSocketController;
    }

    /**
     * Endpoint for inporting Routes from YAML file
     *
     * @param file YAML-file with routes
     * @return status of operation
     */
    @PostMapping("/import")
    public ResponseEntity<String> importRoutes(@RequestParam("file") MultipartFile file) {
        ImportHistory history = new ImportHistory();
        if (file.isEmpty()) {
            history.setStatus(ImportStatus.FAILURE);
            history.setErrorMessage("File not selected for import (or file is empty).");
            importHistoryRepository.save(history);
            return new ResponseEntity<>("File not selected for import.", HttpStatus.BAD_REQUEST);
        }
        try {
            routeImportService.importRoutes(file, history);
            history.setStatus(ImportStatus.SUCCESS);
            importHistoryRepository.save(history);
            routeWebSocketController.notifyImportHistoryChange(new ImportHistoryUpdateDTO(history));
            routeWebSocketController.notifyRouteChange(new RouteUpdateDTO(OperationType.CREATE, history.getRecordsImported(), null));
            return new ResponseEntity<>("Routes successfully imported.", HttpStatus.OK);
        } catch (Exception e) {
            history.setStatus(ImportStatus.FAILURE);
            history.setErrorMessage(e.getMessage());
            importHistoryRepository.save(history);
            routeWebSocketController.notifyImportHistoryChange(new ImportHistoryUpdateDTO(history));
            return new ResponseEntity<>("Error importing routes: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint для получения истории импорта.
     * 
     * Если пользователь имеет роль ADMIN, возвращает все записи.
     * Иначе возвращает только записи, созданные текущим пользователем.
     *
     * @param authentication объект Authentication, предоставляемый Spring Security
     * @return список историй импорта
     */
    @GetMapping("/import")
    public ResponseEntity<List<ImportHistoryUpdateDTO>> getImportHistories() {
        List<ImportHistoryUpdateDTO> dtoList = routeImportService.getImportHistory();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }
}