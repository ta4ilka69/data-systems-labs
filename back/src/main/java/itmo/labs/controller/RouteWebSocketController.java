package itmo.labs.controller;

import itmo.labs.dto.RouteUpdateDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RouteWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public RouteWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyRouteChange(RouteUpdateDTO updateDTO) {
        messagingTemplate.convertAndSend("/topic/routes", updateDTO);
    }
}