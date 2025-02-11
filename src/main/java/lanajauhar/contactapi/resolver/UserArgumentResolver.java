package lanajauhar.contactapi.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lanajauhar.contactapi.entity.User;
import lanajauhar.contactapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        String token = servletRequest.getHeader("X-API-TOKEN");
        log.info("X-API-TOKEN: {}", token);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tidak Terdaftar");
        }

        User user= userRepo.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tidak Terdaftar"));
        log.info("USER: {}", user);

        if (user.getTokenExpiredAt() < System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Habis");
        }
        return user;

    }
}
