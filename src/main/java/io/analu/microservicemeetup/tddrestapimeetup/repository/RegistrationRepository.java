package io.analu.microservicemeetup.tddrestapimeetup.repository;

import io.analu.microservicemeetup.tddrestapimeetup.model.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    boolean existsByRegistration(String registration);

}
