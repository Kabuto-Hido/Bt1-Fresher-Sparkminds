package com.bt1.qltv1.service;

import com.bt1.qltv1.dto.register.RegisterRequest;
import com.bt1.qltv1.entity.Role;
import com.bt1.qltv1.exception.BadRequest;
import com.bt1.qltv1.repository.RoleRepository;
import com.bt1.qltv1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class RegisterServiceTest {

    @InjectMocks
    private RegisterService registerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void register() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .fullname("Lam")
                .email("lam@gmail.com")
                .password("Lam123*")
                .phone("+84097653127").build();

        Role userRole = new Role();

        Mockito.when(roleRepository.findById(1L))
                        .thenReturn(Optional.of(userRole));


    }
}