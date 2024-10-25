package itmo.labs.dto;

import itmo.labs.model.OperationType;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RouteUpdateDTO {
    private OperationType action;
    private Integer routeId;
    @Valid
    private RouteDTO routeDTO;

    @Override
    public String toString() {
        return "RouteUpdateDTO{" +
                "action='" + action + '\'' +
                ", routeId=" + routeId +
                '}';
    }
}