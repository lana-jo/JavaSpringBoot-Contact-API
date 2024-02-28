package lanajauhar.contactapi.service;

import lanajauhar.contactapi.entity.Address;
import lanajauhar.contactapi.entity.Contact;
import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.AddressResponse;
import lanajauhar.contactapi.model.CreateAddresRequest;
import lanajauhar.contactapi.model.UpdateAddresRequest;
import lanajauhar.contactapi.repository.AddressRepository;
import lanajauhar.contactapi.repository.ContactRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    private final ValidationService validationService;
    private final ContactRepository contactRepository;
    private final AddressRepository addressRepository;

    public AddressService(ValidationService validationService, ContactRepository contactRepository,
                          AddressRepository addressRepository) {
        this.validationService = validationService;
        this.contactRepository = contactRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public AddressResponse create(User user, CreateAddresRequest req) {
        validationService.validate(req);

        Contact contact = contactRepository
                .findFirstByUserAndId(user, req.getContactId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(req.getStreet());
        address.setCity(req.getCity());
        address.setProvince(req.getProvince());
        address.setCountry(req.getCountry());
        address.setPostalCode(req.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional(readOnly = true)
    public AddressResponse thisAddress(User user, String contactId, String addressId) {
//        validationService.validate(addressId);
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));
        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat Tidak Ditemukan"));
        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse updateAddress(User user, UpdateAddresRequest req) {
        validationService.validate(req);

        Contact contact = contactRepository.findFirstByUserAndId(user, req.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));
        Address address = addressRepository.findFirstByContactAndId(contact, req.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat Tidak Ditemukan"));

        address.setStreet(req.getStreet());
        address.setCity(req.getCity());
        address.setProvince(req.getProvince());
        address.setCountry(req.getCountry());
        address.setPostalCode(req.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional
    public void deleteAddress(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));
        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alamat Tidak Ditemukan"));
        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));

        List<Address> addressList = addressRepository.findAllByContact(contact);

        return addressList.stream().map(this::toAddressResponse).toList();
    }


    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .country(address.getCountry())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .build();
    }

}
