package lanajauhar.contactapi.controller;

import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.RegisterUserRequest;
import lanajauhar.contactapi.model.UpdateUserRequest;
import lanajauhar.contactapi.model.UserResponse;
import lanajauhar.contactapi.model.WebResponse;
import lanajauhar.contactapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/api/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/api/currentUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> currentUser(User user) {
        UserResponse current = userService.current(user);

        return WebResponse.<UserResponse>builder().data(current).build();
    }

    @PatchMapping(path = "/api/currentUser"
            , consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest req) {
        UserResponse update = userService.update(user, req);
        return WebResponse.<UserResponse>builder().data(update).build();


    }
}
