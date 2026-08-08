package it.personarum.service;

import it.personarum.domain.user.Role;
import it.personarum.domain.user.UserAccount;
import it.personarum.repository.UserAccountRepository;
import it.personarum.service.exception.UserAccountNotFoundException;
import it.personarum.service.exception.UserAccountUsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserAccountService userAccountService;

    @BeforeEach
    void setUp() {
        userAccountService = new UserAccountService(userAccountRepository, passwordEncoder);
    }

    @Test
    void shouldCreateUser() {
        when(userAccountRepository.existsByUsernameIgnoreCase("Operator")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("{bcrypt}hash");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount result = userAccountService.create("Operator", "password123", Role.OPERATOR);

        assertThat(result.getUsername()).isEqualTo("operator");
        assertThat(result.getPasswordHash()).isEqualTo("{bcrypt}hash");
        assertThat(result.getRole()).isEqualTo(Role.OPERATOR);
        assertThat(result.isEnabled()).isTrue();

        verify(passwordEncoder).encode("password123");
        verify(userAccountRepository).save(any(UserAccount.class));
    }

    @Test
    void shouldRejectDuplicateUsername() {
        when(userAccountRepository.existsByUsernameIgnoreCase("admin")).thenReturn(true);

        assertThatThrownBy(() -> userAccountService.create("admin", "password123", Role.ADMIN))
            .isInstanceOf(UserAccountUsernameAlreadyExistsException.class);

        verify(passwordEncoder, never()).encode(anyString());
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void shouldFindAllUsers() {
        UserAccount admin = UserAccount.create("admin", "hash", Role.ADMIN);
        UserAccount operator = UserAccount.create("operator", "hash", Role.OPERATOR);

        when(userAccountRepository.findAll()).thenReturn(List.of(admin, operator));
        List<UserAccount> result = userAccountService.findAll();
        assertThat(result).containsExactly(admin, operator);
    }

    @Test
    void shouldFindUserById() {
        UserAccount user = UserAccount.create("operator", "hash", Role.OPERATOR);
        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(userAccountService.findById(1L)).isSameAs(user);
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(userAccountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountService.findById(99L))
            .isInstanceOf(UserAccountNotFoundException.class);
    }

    @Test
    void shouldUpdateRoleAndEnabledState() {
        UserAccount user = UserAccount.create("operator", "hash", Role.OPERATOR);

        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userAccountRepository.save(user)).thenReturn(user);

        UserAccount result = userAccountService.update(1L, Role.ADMIN, false);

        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    void shouldEnableUser() {
        UserAccount user = UserAccount.create("operator", "hash", Role.OPERATOR);
        user.disable();

        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userAccountRepository.save(user)).thenReturn(user);

        UserAccount result = userAccountService.update(1L, Role.OPERATOR, true);
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void shouldChangePassword() {
        UserAccount user = UserAccount.create("operator", "old-hash", Role.OPERATOR);

        when(userAccountRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("{bcrypt}new-hash");
        when(userAccountRepository.save(user)).thenReturn(user);

        UserAccount result = userAccountService.changePassword(1L, "newPassword123");

        assertThat(result.getPasswordHash()).isEqualTo("{bcrypt}new-hash");
        verify(passwordEncoder).encode("newPassword123");
    }
}
