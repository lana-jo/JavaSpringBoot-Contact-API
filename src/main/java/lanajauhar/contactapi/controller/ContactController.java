package lanajauhar.contactapi.controller;

import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.*;
import lanajauhar.contactapi.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping(path = "/api/contacts/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> create(User user, @RequestBody CreateContactRequest req) {
        ContactResponse contactResponse = contactService.create(user, req);
        return WebResponse.<ContactResponse>builder()
                .data(contactResponse).build();
    }


    @GetMapping(path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String contactId) {
        ContactResponse contactResponse = contactService.get(user, contactId);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @PutMapping(path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<ContactResponse> update(User user, @RequestBody UpdateContactRequest req,
                                               @PathVariable("contactId") String contactId) {
        req.setId(contactId);
        ContactResponse contactResponse = contactService.update(user, req);
        return WebResponse.<ContactResponse>builder()
                .data(contactResponse).build();
    }

    @DeleteMapping(path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> delete(User user,
                                      @PathVariable("contactId") String contactId) {
        contactService.delete(user, contactId);
        return WebResponse.<String>builder()
                .data("OK").build();
    }

    /*public Page<ContactResponse> ListContact(User user, Pageable pageable) {
        return null;
    }
    */

    /*public WebResponse<List<ContactResponse>> getAll(User user) {

    }*/

    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<List<ContactResponse>> search(User user,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "email", required = false) String email,
                                                     @RequestParam(value = "phone", required = false) String phone,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size
    ) {
        SearchContactRequest req = new SearchContactRequest().builder()
                .name(name)
                .email(email)
                .phone(phone)
                .page(page)
                .size(size)
                .build();

        Page<ContactResponse> search = contactService.search(user, req);
        return WebResponse.<List<ContactResponse>>builder()
                .data(search.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(search.getNumber())
                        .totalPage(search.getTotalPages())
                        .size(search.getSize())
                        .build())
                .build();
    }

}
