package lanajauhar.contactapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "contacts", indexes = {
        @Index(name = "fk_users_contacts", columnList = "username")
})
public class Contact {

    @Id
//    @Size(max = 100)
//    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Size(max = 100)
    @NotNull
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Size(max = 100)
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Size(max = 100)
    @Column(name = "phone", length = 100)
    private String phone;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

    @OneToMany(mappedBy = "contact")
    private List<Address> addresses;

}