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

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
        assertThat(savedRegistration.getDateOfRegistration()).isEqualTo("19/10/2022");
        assertThat(savedRegistration.getVersion()).isEqualTo("001");

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

        assertThat(foundRegistration.get().getVersion())
                .isEqualTo(registration.getVersion());
    }

    @Test
    @DisplayName("Deve retornar empty quando o Registration nao existir")
    public void registrationNotFoungById(){

        Integer id = 11;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Registration> registration = registrationService.getRegistrationById(id);

        assertThat(registration.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um registro")
    public void deleteRegistration(){

        Registration registration = Registration.builder().id(11).build();

        assertDoesNotThrow(()-> registrationService.delete(registration));

        Mockito.verify(repository, Mockito.times(1)).delete(registration);
    }

    @Test
    @DisplayName("Deve atualizar um registro")
    private void updateRegistration(){
        Integer id = 11;
        Registration updatingRegistration = Registration.builder().id(id).build();

        //execucao
        Registration updatedRegistration = createValidRegistration();
        updatedRegistration.setId(id);

        Mockito.when(repository.save(updatingRegistration))
                .thenReturn(updatedRegistration);

        Registration registration = registrationService.update(updatingRegistration);

        //asserts

        assertThat(registration.getId())
                .isEqualTo(updatedRegistration.getId());

        assertThat(registration.getName())
                .isEqualTo(updatedRegistration.getName());

        assertThat(registration.getDateOfRegistration())
                .isEqualTo(updatedRegistration.getDateOfRegistration());

        assertThat(registration.getVersion())
                .isEqualTo(updatedRegistration.getVersion());

    }

    @Test
    @DisplayName("Deve filtrar registro por propriedade")
    public void findRegistrationByAnyProperty(){
        //cenario
        Registration registration = createValidRegistration();
        PageRequest pageRequest = PageRequest.of(0,10);

        List<Registration> listRegistration = Arrays.asList(registration);
        Page<Registration> page = new PageImpl<Registration>(Arrays.asList(registration),
                PageRequest.of(0,10),1);

        //execucao
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Registration> result = registrationService.find(registration, pageRequest);

        //assert
        assertThat(result.getTotalElements())
                .isEqualTo(1);
        assertThat(result.getContent())
                .isEqualTo(listRegistration);
        assertThat(result.getPageable().getPageNumber())
                .isEqualTo(0);
        assertThat(result.getPageable().getPageSize())
                .isEqualTo(10);

    }

    @Test
    @DisplayName("Deve retornar um registro pela versao")
    public void getRegistrationByVersion(){

        String  version = "1234";

        Mockito.when(repository.findByVersion(version))
                .thenReturn(Optional.of(Registration.builder().id(11).version(version).build()));

        Optional<Registration> registration = registrationService.getRegistrationByVersion(version);

        assertThat(registration.isPresent())
                .isTrue();
        assertThat(registration.get().getId())
                .isEqualTo(11);
        assertThat(registration.get().getVersion())
                .isEqualTo(version);

        Mockito.verify(repository, Mockito.times(1)).findByVersion(version);



    }

    private Registration createValidRegistration() {
        return Registration.builder()
                .id(101)
                .name("Ana Luiza")
                .dateOfRegistration("19/10/2022")
                .version("001")
                .build();
    }

}
