package itmo.labs.service;

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

import itmo.labs.dto.ImportHistoryUpdateDTO;
import itmo.labs.dto.RouteDTO;
import itmo.labs.model.ImportHistory;
import itmo.labs.model.Role;
import itmo.labs.model.User;
import itmo.labs.repository.ImportHistoryRepository;
import itmo.labs.repository.RouteRepository;
import itmo.labs.repository.UserRepository;
import itmo.labs.utils.YamlRouteParser;
import java.time.LocalDateTime;

@Service
public class RouteImportService {

    private final RouteRepository routeRepository;
    private final RouteService routeService;
    private final ImportHistoryRepository importHistoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public RouteImportService(RouteRepository routeRepository, RouteService routeService,
            ImportHistoryRepository importHistoryRepository, UserRepository userRepository) {
        this.routeRepository = routeRepository;
        this.routeService = routeService;
        this.importHistoryRepository = importHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void importRoutes(MultipartFile file, ImportHistory history) throws Exception {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));
        history.setTimestamp(LocalDateTime.now());
        history.setPerformedBy(currentUser.getUsername());
        importHistoryRepository.save(history);
        try (InputStream inputStream = file.getInputStream()) {
            List<RouteDTO> routeDTOs = YamlRouteParser.parseRoutes(inputStream);
            // Проверка уникальности имен маршрутов в файле
            Set<String> names = new HashSet<>();
            for (RouteDTO dto : routeDTOs) {
                if (!names.add(dto.getName())) {
                    throw new IllegalArgumentException("Duplicate route name found in import file: " + dto.getName());
                }
            }

            // Проверка уникальности имен маршрутов в базе данных
            List<String> existingNames = routeRepository.findAll()
                    .stream()
                    .map(itmo.labs.model.Route::getName)
                    .toList();

            for (RouteDTO dto : routeDTOs) {
                if (existingNames.contains(dto.getName())) {
                    throw new IllegalArgumentException("Route name already exists in the system: " + dto.getName());
                }
                // Проверка координат
                if (dto.getCoordinates().getX() < -180 || dto.getCoordinates().getX() > 180) {
                    throw new IllegalArgumentException("Invalid X (latitude) for route: " + dto.getName());
                }
                if (dto.getCoordinates().getY() < -90 || dto.getCoordinates().getY() > 90) {
                    throw new IllegalArgumentException("Invalid Y (longitude) for route: " + dto.getName());
                }
            }
            // Транзакция: сохранение всех маршрутов
            for (RouteDTO dto : routeDTOs) {
                routeService.createRoute(dto);
            }
            history.setRecordsImported(routeDTOs.size());
        } catch (Exception e) {
            throw e;
        }
    }

    public List<ImportHistoryUpdateDTO> getImportHistory() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUsername));

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