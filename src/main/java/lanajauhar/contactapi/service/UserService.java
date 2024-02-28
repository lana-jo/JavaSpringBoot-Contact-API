package lanajauhar.contactapi.service;

import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.RegisterUserRequest;
import lanajauhar.contactapi.model.UpdateUserRequest;
import lanajauhar.contactapi.model.UserResponse;
import lanajauhar.contactapi.repository.UserRepository;
import lanajauhar.contactapi.security.BCrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepo;


    @Autowired
    private ValidationService validationService;

    @Transactional
    public void register(RegisterUserRequest request) {
      validationService.validate(request);
        //registrasi gagal
        if (userRepo.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username Sudah Terdaftar");
        }
        //registrasi sukses
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepo.save(user);
    }

    @Transactional
    public UserResponse current(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName()).build();
    }

    @Transactional
    public UserResponse update (User user, UpdateUserRequest req) {
        if (Objects.nonNull(req.getName())){
            user.setName(req.getName());
        }

        if (Objects.nonNull(req.getPassword())){
            user.setPassword(BCrypt.hashpw(req.getPassword(), BCrypt.gensalt()));
        }

        userRepo.save(user);
        log.info("User {} updated", user.getName());

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

}
