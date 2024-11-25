package itmo.labs.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import itmo.labs.dto.RouteDTO;
import itmo.labs.model.Route;
import itmo.labs.repository.RouteRepository;

@Service
public class RouteImportService {

    private final RouteRepository routeRepository;
    private final RouteService routeService;

    public RouteImportService(RouteRepository routeRepository, RouteService routeService) {
        this.routeRepository = routeRepository;
        this.routeService = routeService;
    }

    @Transactional
    public void importRoutes(MultipartFile file) throws Exception {
        Yaml yaml = new Yaml(new Constructor(RouteDTO.class, null));
        List<RouteDTO> routeDTOs;
        try (InputStream inputStream = file.getInputStream()) {
            Iterable<Object> iterable = yaml.loadAll(inputStream);
            routeDTOs = new ArrayList<>();
            for (Object obj : iterable) {
                if (obj instanceof RouteDTO) {
                    routeDTOs.add((RouteDTO) obj);
                }
            }
        }
        Set<String> names = new HashSet<>();
        for (RouteDTO dto : routeDTOs) {
            if (!names.add(dto.getName())) {
                throw new IllegalArgumentException("Duplicate route name found in import file: " + dto.getName());
            }
        }
        // Проверка уникальности имен маршрутов в базе данных
        List<String> existingNames = routeRepository.findAll()
                .stream()
                .map(Route::getName)
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
        for (RouteDTO dto : routeDTOs) {
            routeService.createRoute(dto);
        }
    }
}