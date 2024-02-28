package lanajauhar.contactapi.service;

import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.model.LoginUserRequest;
import lanajauhar.contactapi.model.TokenResponse;
import lanajauhar.contactapi.repository.UserRepository;
import lanajauhar.contactapi.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ValidationService validationService;


    @Transactional
    public TokenResponse login(LoginUserRequest loginReq) {
        validationService.validate(loginReq);
        // user tidak ditemukan
        User user = userRepo.findById(loginReq.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username atau password Salah"));

        if (BCrypt.checkpw(loginReq.getPassword(), user.getPassword())) {
            //sukses
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(expiredDate());
            userRepo.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expired_at(user.getTokenExpiredAt()).build();
        } else {
            // sandi salah
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username atau password Salah");
        }
    }


    @Transactional
    public void Logout(User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepo.save(user);
    }

    private Long expiredDate() {
        return System.currentTimeMillis() + (100000 * 16 * 24 * 30);
//        return System.currentTimeMillis()/1000;
    }


}
