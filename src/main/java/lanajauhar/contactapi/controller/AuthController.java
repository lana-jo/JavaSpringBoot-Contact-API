package lanajauhar.contactapi.controller;

import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.LoginUserRequest;
import lanajauhar.contactapi.model.TokenResponse;
import lanajauhar.contactapi.model.WebResponse;
import lanajauhar.contactapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest req) {
        TokenResponse tokenResponse = authService.login(req);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

    @DeleteMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> logout(User user) {
        authService.Logout(user);
        return WebResponse.<String>builder().data("OK").build();
    }

}
