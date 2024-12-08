package itmo.labs.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import itmo.labs.dto.CoordinatesDTO;
import itmo.labs.dto.ImportHistoryUpdateDTO;
import itmo.labs.dto.LocationDTO;
import itmo.labs.dto.RouteDTO;
import itmo.labs.dto.YamlUploadDTO;
import itmo.labs.model.ImportHistory;
import itmo.labs.model.Role;
import itmo.labs.model.User;
import itmo.labs.repository.ImportHistoryRepository;
import itmo.labs.utils.YamlRouteParser;
import java.time.LocalDateTime;

@Service
public class RouteImportService {

    private final RouteService routeService;
    private final ImportHistoryRepository importHistoryRepository;
    private final UserService userService;
    private final CoordinatesService coordinatesService;
    private final LocationService locationService;
    private final MinioClient minioClient;

    @Autowired
    public RouteImportService(RouteService routeService,
            ImportHistoryRepository importHistoryRepository, UserService userService,
            CoordinatesService coordinatesService, LocationService locationService,
            MinioClient minioClient) {
        this.routeService = routeService;
        this.importHistoryRepository = importHistoryRepository;
        this.userService = userService;
        this.coordinatesService = coordinatesService;
        this.locationService = locationService;
        this.minioClient = minioClient;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importRoutes(MultipartFile file, ImportHistory history) throws Exception {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser;
        try {
            currentUser = userService.getUserByUsername(currentUsername);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User not found: " + currentUsername);
        }

        history.setTimestamp(LocalDateTime.now());
        history.setPerformedBy(currentUser.getUsername());

        // Generate a unique filename, e.g., using UUID
        String userFileName = currentUser.getUsername() +"_" + System.currentTimeMillis() + "/" + file.getOriginalFilename();

        try {
            // Upload file to MinIO
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket("ta4ilka-drive")
                                .object(userFileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            // Set the file URL in history
            String fileUrl = minioClient.getPresignedObjectUrl(
                    io.minio.GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket("ta4ilka-drive")
                            .object(userFileName)
                            .build()
            );
            history.setFileUrl(fileUrl);
        } catch (MinioException e) {
            throw new Exception("Error uploading file to MinIO: " + e.getMessage());
        }

        importHistoryRepository.save(history);

        try (InputStream inputStream = file.getInputStream()) {
            YamlUploadDTO importDTO = YamlRouteParser.parseYamlFile(inputStream);
            int totalImported = 1;

            // Import Coordinates
            try {
                if (importDTO.getCoordinates() != null) {
                    for (CoordinatesDTO coord : importDTO.getCoordinates()) {
                        coordinatesService.saveCoordinates(RouteDTO.convertToEntity(coord));
                        totalImported++;
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Error importing coordinates on number " + totalImported + ": " + e.getMessage());
            }

            // Import Locations
            try {
                if (importDTO.getLocations() != null) {
                    for (LocationDTO loc : importDTO.getLocations()) {
                        locationService.saveLocation(RouteDTO.convertToEntity(loc));
                        totalImported++;
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Error importing locations on number " + totalImported + ": " + e.getMessage());
            }

            // Validate and Import Routes
            if (importDTO.getRoutes() != null) {
                Set<String> names = new HashSet<>();
                for (RouteDTO dto : importDTO.getRoutes()) {
                    if (!names.add(dto.getName())) {
                        throw new IllegalArgumentException(
                                "Duplicate route name found in import file: " + dto.getName());
                    }
                }

                // Validate coordinates
                for (RouteDTO dto : importDTO.getRoutes()) {
                    if (dto.getCoordinates().getX() < -180 || dto.getCoordinates().getX() > 180) {
                        throw new IllegalArgumentException("Invalid X (latitude) for route: " + dto.getName());
                    }
                    if (dto.getCoordinates().getY() < -90 || dto.getCoordinates().getY() > 90) {
                        throw new IllegalArgumentException("Invalid Y (longitude) for route: " + dto.getName());
                    }
                }

                // Import Routes
                for (RouteDTO dto : importDTO.getRoutes()) {
                    dto.setCreatedById(currentUser.getId());
                    dto.setCreatedByUsername(currentUser.getUsername());
                    try {
                        routeService.createRoute(dto);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "Error importing routes on number " + totalImported + ": " + e.getMessage());
                    }
                    totalImported++;
                }
            }

            history.setRecordsImported(totalImported);
            importHistoryRepository.save(history);

        } catch (Exception e) {
            // If any exception occurs, delete the uploaded file to maintain consistency
            history.setFileUrl(null);
            try {
                minioClient.removeObject(
                        io.minio.RemoveObjectArgs.builder()
                                .bucket("ta4ilka-drive")
                                .object(userFileName)
                                .build()
                );
            } catch (Exception ex) {
                // Log this exception
                System.err.println("Failed to delete file from MinIO after rollback: " + ex.getMessage());
            }
            throw e;
        }
    }

    public List<ImportHistoryUpdateDTO> getImportHistory() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser;
        try {
            currentUser = userService.getUserByUsername(currentUsername);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User not found: " + currentUsername);
        }

        List<ImportHistory> histories;

        if (currentUser.getRoles().contains(Role.ADMIN)) {
            histories = importHistoryRepository.findAll();
        } else {
            histories = importHistoryRepository.findByPerformedBy(currentUsername);
        }
        List<ImportHistoryUpdateDTO> dtoList = histories.stream()
                .map(ImportHistoryUpdateDTO::new)
                .collect(Collectors.toList());
        return dtoList;
    }
}