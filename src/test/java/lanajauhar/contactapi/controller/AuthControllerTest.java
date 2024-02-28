package lanajauhar.contactapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.LoginUserRequest;
import lanajauhar.contactapi.model.TokenResponse;
import lanajauhar.contactapi.model.WebResponse;
import lanajauhar.contactapi.repository.UserRepository;
import lanajauhar.contactapi.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
    }

    @Test
    void loginFailedUserNotFound() throws Exception {
        LoginUserRequest loginReq = new LoginUserRequest();
        loginReq.setUsername("username");
        loginReq.setPassword("password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrorMessage());
        });
    }

    @Test
    void loginFailedWrongPW() throws Exception {

        User user = new User();
        user.setUsername("username");
        user.setName("name");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepo.save(user);

        LoginUserRequest loginReq = new LoginUserRequest();
        loginReq.setUsername("username");
        loginReq.setPassword("tidaksesuai");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrorMessage());
        });
    }
    @Test
    void loginSucces() throws Exception {

        User user = new User();
        user.setUsername("username");
        user.setName("name");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepo.save(user);

        LoginUserRequest loginReq = new LoginUserRequest();
        loginReq.setUsername("username");
        loginReq.setPassword("password");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrorMessage());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpired_at());

            //query ke database
            User userdb = userRepo.findById("username").orElse(null);
            assertNotNull(userdb);
            assertEquals(userdb.getToken(), response.getData().getToken());
            assertEquals(userdb.getTokenExpiredAt(), response.getData().getExpired_at());

        });
    }

    @Test
    void LogoutFailed() throws Exception {
    mockMvc.perform(
            delete("/api/auth/logout")
                    .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(response.getErrorMessage());
    });
    }

    @Test
    void LogoutSucces() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis()* 1000000L);
        userRepo.save(user);

    mockMvc.perform(
            delete("/api/auth/logout")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
        WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNull(response.getErrorMessage());
        assertEquals("OK", response.getData());

        User userdb = userRepo.findById("test").orElse(null);
        assertNotNull(userdb);
        assertNull(userdb.getToken());
        assertNull(userdb.getTokenExpiredAt());
    });
    }


}