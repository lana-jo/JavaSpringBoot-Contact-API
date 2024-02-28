package lanajauhar.contactapi.service;

import jakarta.persistence.criteria.Predicate;
import lanajauhar.contactapi.entity.Contact;
import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.ContactResponse;
import lanajauhar.contactapi.model.CreateContactRequest;
import lanajauhar.contactapi.model.SearchContactRequest;
import lanajauhar.contactapi.model.UpdateContactRequest;
import lanajauhar.contactapi.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ContactService {


    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public ContactResponse create(User user, CreateContactRequest req) {

        validationService.validate(req);
        Contact contact = new Contact();

        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(req.getFirstName());
        contact.setLastName(req.getLastName());
        contact.setEmail(req.getEmail());
        contact.setPhone(req.getPhone());
        contact.setUser(user);
        contactRepository.save(contact);

        return toContactResponse(contact);
    }

    @Transactional(readOnly = true)
    public ContactResponse get(User user, String id) {

        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));
//                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return toContactResponse(contact);
    }


    @Transactional
    public ContactResponse update(User user, UpdateContactRequest req) {
        validationService.validate(req);
        Contact contact = contactRepository.findFirstByUserAndId(user, req.getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));

        contact.setFirstName(req.getFirstName());
        contact.setLastName(req.getLastName());
        contact.setEmail(req.getEmail());
        contact.setPhone(req.getPhone());
        contactRepository.save(contact);
        return toContactResponse(contact);
    }

    @Transactional
    public void delete(User user, String id) {
//        validationService.validate(req);
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kontak Tidak Ditemukan"));
        contactRepository.delete(contact);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest req) {
        Specification<Contact> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"), user));
            /*if (req.getName() != null && !req.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + req.getName() + "%"));
            }*/
            if (Objects.nonNull(req.getName())) {
//                predicates.add(criteriaBuilder.like(root.get("name"), "%" + req.getName() + "%"));
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("firstName"), "%" + req.getName() + "%"),
                        criteriaBuilder.like(root.get("lastName"), "%" + req.getName() + "%")
//                        criteriaBuilder.like(root.get("phone"), "%" + req.getPhone() + "%"),
//                        criteriaBuilder.like(root.get("email"), "%" + req.getEmail() + "%")
                ));
            }
            if (Objects.nonNull(req.getPhone())) {
//                predicates.add(criteriaBuilder.equal(root.get("phone"), req.getPhone()));
//                predicates.add(criteriaBuilder.or(criteriaBuilder.like(root.get("phone"), "%" + req.getPhone())));
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + req.getPhone()+ "%"));
            }
            if (Objects.nonNull(req.getEmail())) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + req.getEmail()+ "%"));
            }
            return criteriaQuery.where(predicates.toArray(new Predicate[] {})).getRestriction();
        };

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());
        Page<Contact> searchContact = contactRepository.findAll(specification, pageable);
//        List<ContactResponse> contactResponses = listContact.getContent().stream();
        List<ContactResponse> contactResponses =  searchContact.getContent().stream()
                .map(this::toContactResponse)
                .toList();
        return new PageImpl<>(contactResponses, pageable, searchContact.getTotalElements());
    }


    public ContactResponse toContactResponse(Contact contact){
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }


}
