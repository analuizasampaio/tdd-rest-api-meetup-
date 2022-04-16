package io.analu.microservicemeetup.tddrestapimeetup.service.impl;

import io.analu.microservicemeetup.tddrestapimeetup.exception.BusinessException;
import io.analu.microservicemeetup.tddrestapimeetup.model.entity.Registration;
import io.analu.microservicemeetup.tddrestapimeetup.repository.RegistrationRepository;
import io.analu.microservicemeetup.tddrestapimeetup.service.RegistrationService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    RegistrationRepository repository ;
    public RegistrationServiceImpl(RegistrationRepository repository) {
        this.repository = repository;
    }


    public Registration save(Registration registration) {
        if (repository.existsByRegistration(registration.getRegistration())){
            throw new BusinessException("Registration already created!");
        }

        return repository.save(registration);
    }
}
