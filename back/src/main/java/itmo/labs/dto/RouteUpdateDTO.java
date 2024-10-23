package itmo.labs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteUpdateDTO {
    private String action;
    private Integer routeId;

    public RouteUpdateDTO() {
    }

    public RouteUpdateDTO(String action, Integer integer) {
        this.action = action;
        this.routeId = integer;
    }

    @Override
    public String toString() {
        return "RouteUpdateDTO{" +
                "action='" + action + '\'' +
                ", routeId=" + routeId +
                '}';
    }
}