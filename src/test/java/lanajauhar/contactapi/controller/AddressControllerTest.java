package lanajauhar.contactapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lanajauhar.contactapi.entity.Address;
import lanajauhar.contactapi.entity.Contact;
import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.AddressResponse;
import lanajauhar.contactapi.model.CreateAddresRequest;
import lanajauhar.contactapi.model.WebResponse;
import lanajauhar.contactapi.repository.AddressRepository;
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
class AddressControllerTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    /*private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final ContactRepository contactRepository;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    AddressControllerTest(UserRepository userRepository, AddressRepository addressRepository, ContactRepository contactRepository, ObjectMapper objectMapper, MockMvc mockMvc) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.contactRepository = contactRepository;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }
*/

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setName("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() * 1000000L);
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setId("test");
        contact.setFirstName("Lana");
        contact.setLastName("Jauhar");
        contact.setEmail("lanajauhar@example.com");
        contact.setPhone("123456789");
        contact.setUser(user);
        contactRepository.save(contact);
    }

    @Test
    void createAddressBadReq() throws Exception {

        CreateAddresRequest req = new CreateAddresRequest();
        req.setCountry("");

        mockMvc.perform(
                post("/api/contact/test/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );

    }

    @Test
    void createAddressSucces() throws Exception {

        CreateAddresRequest req = new CreateAddresRequest();
        req.setStreet("Jalan");
        req.setCity("Semarang");
        req.setProvince("Jawa Tengah");
        req.setCountry("Indonesia");
        req.setPostalCode("51000");

        mockMvc.perform(
                post("/api/contact/test/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(req.getStreet(), response.getData().getStreet());
                    assertEquals(req.getCity(), response.getData().getCity());
                    assertEquals(req.getProvince(), response.getData().getProvince());
                    assertEquals(req.getCountry(), response.getData().getCountry());
                    assertEquals(req.getPostalCode(), response.getData().getPostalCode());

                    assertTrue(addressRepository.existsById(response.getData().getId()));
                }
        );

    }

    @Test
    void getAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact/test/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );

    }

    @Test
    void getAddressSucces() throws Exception {

        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setStreet("jalan");
        address.setCity("Semarang");
        address.setProvince("Jawa Tengah");
        address.setCountry("Indonesia");
        address.setPostalCode("51000");
        address.setContact(contact);

        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contact/test/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(address.getStreet(), response.getData().getStreet());
                    assertEquals(address.getCity(), response.getData().getCity());
                    assertEquals(address.getProvince(), response.getData().getProvince());
                    assertEquals(address.getCountry(), response.getData().getCountry());
                    assertEquals(address.getPostalCode(), response.getData().getPostalCode());

//                    assertTrue(addressRepository.existsById(response.getData().getId()));
                }
        );

    }


    @Test
    void updateAddressBadReq() throws Exception {

        CreateAddresRequest req = new CreateAddresRequest();
        req.setCountry("");

        mockMvc.perform(
        put("/api/contact/test/addresses/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .header("X-API-TOKEN", "test")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );

    }


    @Test
    void updateAddressSucces() throws Exception {


        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setStreet("jalan");
        address.setCity("Semarang");
        address.setProvince("Jawa Tengah");
        address.setCountry("Indonesia");
        address.setPostalCode("51000");
        address.setContact(contact);

        addressRepository.save(address);

        CreateAddresRequest req = new CreateAddresRequest();
        req.setStreet("newJalan");
        req.setCity("new Semarang");
        req.setProvince("new Jawa Tengah");
        req.setCountry("new Indonesia");
        req.setPostalCode("new 51000");

        mockMvc.perform(
                put("/api/contact/test/addresses/test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(req.getStreet(), response.getData().getStreet());
                    assertEquals(req.getCity(), response.getData().getCity());
                    assertEquals(req.getProvince(), response.getData().getProvince());
                    assertEquals(req.getCountry(), response.getData().getCountry());
                    assertEquals(req.getPostalCode(), response.getData().getPostalCode());

                    assertTrue(addressRepository.existsById(response.getData().getId()));
                }
        );
    }

    @Test
    void deleteAddressBadReq() throws Exception {
        mockMvc.perform(
                delete("/api/contact/test/addresses/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );

    }

    @Test
    void deleteAddressSucces() throws Exception {

        Contact contact = contactRepository.findById("test").orElseThrow();

        Address address = new Address();
        address.setId("test");
        address.setStreet("jalan");
        address.setCity("Semarang");
        address.setProvince("Jawa Tengah");
        address.setCountry("Indonesia");
        address.setPostalCode("51000");
        address.setContact(contact);

        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contact/test/addresses/test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals("OK", response.getData());

                    assertFalse(addressRepository.existsById("test"));
                }
        );

    }


    @Test
    void listAddressNotFound() throws Exception {
        mockMvc.perform(
                get("/api/contact/tidak/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(
                result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrorMessage());
                }
        );

    }


    @Test
    void listAddressSucces() throws Exception {

        Contact contact = contactRepository.findById("test").orElseThrow();

        for (int i = 0; i < 10000 ; i++) {

        Address address = new Address();
        address.setId("test" + i);
        address.setStreet("jalan" + i);
        address.setCity("Semarang" + i);
        address.setProvince("Jawa Tengah" + i);
        address.setCountry("Indonesia" + i);
        address.setPostalCode("51000"+ i);
        address.setContact(contact);

        addressRepository.save(address);
        }

        mockMvc.perform(
                get("/api/contact/test/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    WebResponse<List<AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNull(response.getErrorMessage());
                    assertEquals(10000, response.getData().size());
                }
        );

    }



}