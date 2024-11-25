package itmo.labs.controller;

import itmo.labs.dto.ImportHistoryUpdateDTO;
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

    @Autowired
    public RouteImportController(RouteImportService routeImportService) {
        this.routeImportService = routeImportService;
    }

    /**
     * Endpoint for inporting Routes from YAML file
     *
     * @param file YAML-file with routes
     * @return status of operation
     */
    @PostMapping("/import")
    public ResponseEntity<String> importRoutes(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("File not selected for import.", HttpStatus.BAD_REQUEST);
        }
        try {
            routeImportService.importRoutes(file);
            return new ResponseEntity<>("Routes successfully imported.", HttpStatus.OK);
        } catch (Exception e) {
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