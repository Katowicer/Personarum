package it.personarum.service;

import it.personarum.domain.user.Role;
import it.personarum.domain.user.UserAccount;
import it.personarum.repository.UserAccountRepository;
import it.personarum.service.exception.UserAccountNotFoundException;
import it.personarum.service.exception.UserAccountUsernameAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount create(String username, String password, Role role) {
        if (userAccountRepository.existsByUsernameIgnoreCase(username)) {
            throw new UserAccountUsernameAlreadyExistsException(username);
        }

        String passwordHash = passwordEncoder.encode(password);
        UserAccount userAccount = UserAccount.create(username, passwordHash, role);

        return userAccountRepository.save(userAccount);
    }

    public List<UserAccount> findAll() {
        return userAccountRepository.findAll();
    }

    public UserAccount findById(Long id) {
        return userAccountRepository
            .findById(id)
            .orElseThrow(() -> new UserAccountNotFoundException(id));
    }

    @Transactional
    public UserAccount update(Long id, Role role, boolean enabled) {
        UserAccount userAccount = findById(id);
        userAccount.changeRole(role);

        if (enabled)    { userAccount.enable(); }
        else            { userAccount.disable(); }

        return userAccountRepository.save(userAccount);
    }

    @Transactional
    public UserAccount changePassword(Long id, String password) {
        UserAccount userAccount = findById(id);

        String passwordHash = passwordEncoder.encode(password);
        userAccount.changePasswordHash(passwordHash);

        return userAccountRepository.save(userAccount);
    }
}
