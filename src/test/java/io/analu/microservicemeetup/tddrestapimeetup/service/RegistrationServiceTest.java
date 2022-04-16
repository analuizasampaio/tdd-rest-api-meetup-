package io.analu.microservicemeetup.tddrestapimeetup.service;

import io.analu.microservicemeetup.tddrestapimeetup.exception.BusinessException;
import io.analu.microservicemeetup.tddrestapimeetup.model.entity.Registration;
import io.analu.microservicemeetup.tddrestapimeetup.repository.RegistrationRepository;
import io.analu.microservicemeetup.tddrestapimeetup.service.impl.RegistrationServiceImpl;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class RegistrationServiceTest {

    RegistrationService registrationService;

    @MockBean
    RegistrationRepository repository;

    @BeforeEach
    public void setUp() {
        this.registrationService = new RegistrationServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um novo registro")
    public void savePerson(){

        //cenario
        Registration registration = createValidRegistration();

        //execução
        Mockito.when(repository.existsByRegistration(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(repository.save(registration))
                .thenReturn(createValidRegistration());

        Registration savedRegistration = registrationService.save(registration);

        // assert (resultados esperados)
        assertThat(savedRegistration.getId()).isEqualTo(101);
        assertThat(savedRegistration.getName()).isEqualTo("Ana Luiza");
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo(LocalDate.now());
        assertThat(savedRegistration.getRegistration()).isEqualTo("001");

    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Ana Luiza")
                .dateOfRegistration(LocalDate.now())
                .registration("001")
                .build();
    }

    @Test
    @DisplayName("Deve retornar business error para registrados duplicados")
    public void shouldNotSaveRegistrationDuplicated(){

        Registration registration = createValidRegistration();
        Mockito.when(repository.existsByRegistration(Mockito.any()))
                .thenReturn(true);

        Throwable exception = Assertions.catchThrowable(()-> registrationService.save(registration));
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Registration already created!");

        Mockito.verify(repository, Mockito.never()).save(registration);
    }

    @Test
    @DisplayName("Deve ter somente um registro por Id")
    public void getByRegistrationId(){
        Integer id = 11;
        Registration registration = createValidRegistration();
        registration.setId(id);
        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(registration));

        //execucao
        Optional<Registration> foundRegistration = registrationService.getRegistrationById(id);

        //asserts
        assertThat(foundRegistration.isPresent())
                .isTrue();

        assertThat(foundRegistration.get().getId())
                .isEqualTo(id);

        assertThat(foundRegistration.get().getName())
                .isEqualTo(registration.getName());

        assertThat(foundRegistration.get().getDateOfRegistration())
                .isEqualTo(registration.getDateOfRegistration());

        assertThat(foundRegistration.get().getRegistration())
                .isEqualTo(registration.getRegistration());
    }
}
