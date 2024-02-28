package lanajauhar.contactapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.RegisterUserRequest;
import lanajauhar.contactapi.model.UpdateUserRequest;
import lanajauhar.contactapi.model.UserResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("coba");
        request.setPassword("password");
        request.setName("lana");


        mvc.perform(
                post("/api/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertEquals("OK", response.getData());
        });
    }
    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest req = new RegisterUserRequest();
        req.setUsername("");
        req.setPassword("");
        req.setName("");


        mvc.perform(
                post("/api/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrorMessage());
        });

    }
    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setName("siapa");
        userRepository.save(user);

        RegisterUserRequest req = new RegisterUserRequest();
        req.setUsername("test");
        req.setPassword("pw");
        req.setName("name");


        mvc.perform(
                post("/api/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrorMessage());
        });

    }

    @Test
    void currentUserTokenNotValid() throws Exception{
        mvc.perform(
                get("/api/currentUser")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "Token Tidak Ditemukan")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrorMessage());
        });
    }

    @Test
    void currentUserTokenNotSend() throws Exception{
        mvc.perform(
                get("/api/currentUser")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrorMessage());
        });
    }

    @Test
    void currentUserSucces() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis()* 1000000L);
        userRepository.save(user);

        mvc.perform(
                get("/api/currentUser")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });
            assertNull(response.getErrorMessage());
            assertEquals("test", response.getData().getUsername());
            assertEquals("test", response.getData().getName());
        });
    }

    @Test
    void currentUserTokenExpired() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 1000000L);
        userRepository.save(user);

        mvc.perform(
                get("/api/currentUser")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            assertNotNull(response.getErrorMessage());
        });
    }

    @Test
    void updateUserTokenNotSend() throws Exception{
        UpdateUserRequest req = new UpdateUserRequest();

        mvc.perform(
                patch("/api/currentUser")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            assertNotNull(response.getErrorMessage());
        });
    }

    @Test
    void updateUserSucces() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis()* 1000000L);
        userRepository.save(user);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setName("Lana");
        req.setPassword("lana12346789");

        mvc.perform(
                patch("/api/currentUser")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });
            assertNull(response.getErrorMessage());
            assertEquals("test", response.getData().getUsername());
            assertEquals("Lana", response.getData().getName());

            User userdb = userRepository.findById("test").orElse(null);
            assertNotNull(userdb);
            assertTrue(BCrypt.checkpw("lana12346789", userdb.getPassword()));

        });
    }

}