package lanajauhar.contactapi.service;


import jakarta.validation.*;
import lanajauhar.contactapi.model.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {

    @Autowired
    public Validator validator;

    public void validate(Object req) throws ConstraintViolationException {

        //validasi jika data kosong
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(req);
        if (!constraintViolations.isEmpty()) {
            //errorMessage
            throw new ConstraintViolationException(constraintViolations);
        }
    }
}
