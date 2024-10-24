package itmo.labs.controller;

import itmo.labs.model.User;
import itmo.labs.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to get all admin role requests
     * 
     * @return list of users who requested admin role
     */
    @GetMapping("/admin-role-requests")
    public ResponseEntity<List<User>> getAllAdminRoleRequests() {
        List<User> requests = userService.getAllAdminRoleRequests();
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    /**
     * 
     * Endpoint to approve an admin role request
     * 
     * @param userId the ID of the user whose request is being approved
     * @return success message
     */

    @PostMapping("/approve-admin-role/{userId}")
    public ResponseEntity<String> approveAdminRoleRequest(@PathVariable Integer userId) {
        userService.approveAdminRoleRequest(userId);
        return new ResponseEntity<>("Admin role approved successfully.", HttpStatus.OK);
    }
}