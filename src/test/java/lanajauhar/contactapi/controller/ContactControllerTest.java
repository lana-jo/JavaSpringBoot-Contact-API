package lanajauhar.contactapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lanajauhar.contactapi.entity.Contact;
import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.ContactResponse;
import lanajauhar.contactapi.model.CreateContactRequest;
import lanajauhar.contactapi.model.UpdateContactRequest;
import lanajauhar.contactapi.model.WebResponse;
import lanajauhar.contactapi.repository.ContactRepository;
import lanajauhar.contactapi.repository.UserRepository;
import lanajauhar.contactapi.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

       /* contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() * 1000000L);
        userRepository.save(user);*/

        User user = new User();
        user.setUsername("test");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() * 1000000L);
        userRepository.save(user);
    }

    @Test
    void createCotactFailed() throws Exception {
        CreateContactRequest req = new CreateContactRequest();
        req.setFirstName("");
        req.setLastName("Salah");

        mockMvc.perform(
                post("/api/contact/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );
    }

    @Test
    void createCotactSucces() throws Exception {
        CreateContactRequest req = new CreateContactRequest();
        req.setFirstName("lana");
        req.setLastName("jauhar");
        req.setEmail("lanajauhar@example.com");
        req.setPhone("123456789");

        mockMvc.perform(
                post("/api/contact/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals("lana", response.getData().getFirstName());
                    assertEquals("jauhar", response.getData().getLastName());
                    assertEquals("lanajauhar@example.com", response.getData().getEmail());
                    assertEquals("123456789", response.getData().getPhone());
                    assertTrue(contactRepository.existsById(response.getData().getId()));
                }
        );
    }

    @Test
    void getCotactFailed() throws Exception {

        mockMvc.perform(
                get("/api/contact/122222")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );
    }


    @Test
    void getCotactSucces() throws Exception {

        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Lana");
        contact.setLastName("Jauhar");
        contact.setEmail("lanajauhar@example.com");
        contact.setPhone("123456789");
        contact.setUser(user);
        contactRepository.save(contact);


        mockMvc.perform(
                get("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(contact.getId(), response.getData().getId());
                    assertEquals(contact.getFirstName(), response.getData().getFirstName());
                    assertEquals(contact.getLastName(), response.getData().getLastName());
                    assertEquals(contact.getEmail(), response.getData().getEmail());
                    assertEquals(contact.getPhone(), response.getData().getPhone());
                    assertTrue(contactRepository.existsById(response.getData().getId()));
                }
        );
    }

    @Test
    void updateCotactFailed() throws Exception {
        UpdateContactRequest req = new UpdateContactRequest();
        req.setFirstName("");
        req.setLastName("Salah");

        mockMvc.perform(
                put("/api/contact/1234")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );
    }

    @Test
    void updateCotactSucces() throws Exception {
        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Lana");
        contact.setLastName("Jauhar");
        contact.setEmail("lanajauhar@example.com");
        contact.setPhone("123456789");
        contactRepository.save(contact);

        CreateContactRequest req = new CreateContactRequest();
        req.setFirstName("berubah lana");
        req.setLastName("berubah jauhar");
        req.setEmail("lanajauhar@berubah.com");
        req.setPhone("987654321");
        mockMvc.perform(
                put("/api/contact/" + contact.getId() )
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsBytes(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(req.getFirstName(), response.getData().getFirstName());
                    assertEquals(req.getLastName(), response.getData().getLastName());
                    assertEquals(req.getEmail(), response.getData().getEmail());
                    assertEquals(req.getPhone(), response.getData().getPhone());
                    assertTrue(contactRepository.existsById(response.getData().getId()));
                }
        );
    }

    @Test
    void deleteCotactFailed() throws Exception {

        mockMvc.perform(
                delete("/api/contact/122222")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );
    }


    @Test
    void deleteCotactSucces() throws Exception {

        User user = userRepository.findById("test").orElseThrow();

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Lana");
        contact.setLastName("Jauhar");
        contact.setEmail("lanajauhar@example.com");
        contact.setPhone("123456789");
        contact.setUser(user);
        contactRepository.save(contact);


        mockMvc.perform(
                delete("/api/contact/" + contact.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals("OK", response.getData());

                }
        );
    }


    @Test
    void searchCotactFailed() throws Exception {

        mockMvc.perform(
                get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(0, response.getData().size());
                    assertEquals(0, response.getPaging().getCurrentPage());
                    assertEquals(0, response.getPaging().getTotalPage());
                    assertEquals(10, response.getPaging().getSize());
                }
        );
    }

    @Test
    void searchContactSucces() throws Exception {

        User user = userRepository.findById("test").orElseThrow();

        for (int i = 0; i < 100; i++) {

        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Lana" + i);
        contact.setLastName("Jauhar");
        contact.setEmail("lanajauhar@example.com");
        contact.setPhone("123456789");
        contact.setUser(user);
        contactRepository.save(contact);
        }


        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("name", "Lana")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(10, response.getData().size());
                    assertEquals(0, response.getPaging().getCurrentPage());
                    assertEquals(10, response.getPaging().getTotalPage());
                    assertEquals(10, response.getPaging().getSize());
                }
        );

        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("name", "Jauhar")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(10, response.getData().size());
                    assertEquals(0, response.getPaging().getCurrentPage());
                    assertEquals(10, response.getPaging().getTotalPage());
                    assertEquals(10, response.getPaging().getSize());
                }
        );
        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("email", "example.com")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(10, response.getData().size());
                    assertEquals(0, response.getPaging().getCurrentPage());
                    assertEquals(10, response.getPaging().getTotalPage());
                    assertEquals(10, response.getPaging().getSize());
                }
        );
        mockMvc.perform(
                get("/api/contacts" )
                        .queryParam("phone", "123456")
                        .queryParam("page", "1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(0, response.getData().size());
                    assertEquals(1000, response.getPaging().getCurrentPage());
                    assertEquals(10, response.getPaging().getTotalPage());
                    assertEquals(10, response.getPaging().getSize());
                }
        );
    }



}