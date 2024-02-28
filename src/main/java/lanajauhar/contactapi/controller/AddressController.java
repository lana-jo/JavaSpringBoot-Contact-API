package lanajauhar.contactapi.controller;

import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.AddressResponse;
import lanajauhar.contactapi.model.CreateAddresRequest;
import lanajauhar.contactapi.model.UpdateAddresRequest;
import lanajauhar.contactapi.model.WebResponse;
import lanajauhar.contactapi.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {


    @Autowired
    private AddressService addressService;

    /*private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }*/

    @PostMapping(path = "/api/contacts/{contactId}/addresses"
            , produces = MediaType.APPLICATION_JSON_VALUE
            , consumes = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> createAdress(User user, @RequestBody CreateAddresRequest req, @PathVariable("contactId") String contactId) {
        req.setContactId(contactId);
        AddressResponse respons = addressService.create(user, req);

        return WebResponse.<AddressResponse>builder().data(respons).build();
    }


    @GetMapping(path = "/api/contacts/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> getAdress(User user, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId) {

        AddressResponse response = addressService.thisAddress(user, contactId, addressId);
        return WebResponse.<AddressResponse>builder().data(response).build();

    }

    @PutMapping(path = "/api/contacts/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<AddressResponse> updateAdress(User user, @RequestBody UpdateAddresRequest req, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId) {

        req.setContactId(contactId);
        req.setAddressId(addressId);

        AddressResponse response = addressService.updateAddress(user, req);
        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @DeleteMapping(path = "/api/contacts/{contactId}/addresses/{addressId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> deleteAddress(User user, @PathVariable("contactId") String contactId, @PathVariable("addressId") String addressId) {

        addressService.deleteAddress(user, contactId, addressId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/api/contacts/{contactId}/addresses"
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<List<AddressResponse>> listAddresses(User user
            , @PathVariable("contactId") String contactId) {
        List<AddressResponse> responses = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder().data(responses).build();

    }

}
