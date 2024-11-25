package itmo.labs.utils;

import itmo.labs.dto.RouteDTO;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.List;

public class YamlRouteParser {

    public static List<RouteDTO> parseRoutes(InputStream inputStream) {
        Yaml yaml = new Yaml(new Constructor(RouteDTOWrapper.class, new LoaderOptions()));
        RouteDTOWrapper wrapper = yaml.load(inputStream);
        return wrapper.getRoutes();
    }

    public static class RouteDTOWrapper {
        private List<RouteDTO> routes;

        public List<RouteDTO> getRoutes() {
            return routes;
        }

        public void setRoutes(List<RouteDTO> routes) {
            this.routes = routes;
        }
    }
}