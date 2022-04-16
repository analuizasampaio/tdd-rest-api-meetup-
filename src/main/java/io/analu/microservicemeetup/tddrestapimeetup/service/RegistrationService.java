package io.analu.microservicemeetup.tddrestapimeetup.service;

import io.analu.microservicemeetup.tddrestapimeetup.model.entity.Registration;

import java.util.Optional;

public interface RegistrationService {

    Registration save(Registration any);

    Optional<Registration> getRegistrationById(Integer id);
}
