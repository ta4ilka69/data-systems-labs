package itmo.labs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteUpdateDTO {
    private String action;
    private Integer routeId;
    private RouteDTO routeDTO;

    public RouteUpdateDTO() {
    }

    public RouteUpdateDTO(String action, Integer integer, RouteDTO routeDTO) {
        this.action = action;
        this.routeId = integer;
        this.routeDTO = routeDTO;
    }

    @Override
    public String toString() {
        return "RouteUpdateDTO{" +
                "action='" + action + '\'' +
                ", routeId=" + routeId +
                '}';
    }
}