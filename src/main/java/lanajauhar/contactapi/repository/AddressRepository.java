package lanajauhar.contactapi.repository;

import lanajauhar.contactapi.entity.Address;
import lanajauhar.contactapi.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, String> {

    Optional<Address> findFirstByContactAndId(Contact contact, String s);

    List<Address> findAllByContact(Contact contact);
}