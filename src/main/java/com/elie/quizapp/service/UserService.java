package com.elie.quizapp.service;

import com.elie.quizapp.entity.User;
import com.elie.quizapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String username, String password, boolean isAdmin) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setAdmin(isAdmin);
        user.setEnabled(true);
        
        return userRepository.save(user);
    }

    public User registerUser(String username, String password) {
        return createUser(username, password, false);
    }

    public User createAdmin(String username, String password) {
        return createUser(username, password, true);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findAllRegularUsers() {
        return userRepository.findAllRegularUsers();
    }

    public List<User> findAllAdmins() {
        return userRepository.findAllAdmins();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User toggleAdminStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setAdmin(!user.isAdmin());
        return userRepository.save(user);
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }

    public long getTotalUserCount() {
        return userRepository.count();
    }

    public long getRegularUserCount() {
        return userRepository.countRegularUsers();
    }

    public long getAdminCount() {
        return userRepository.countAdmins();
    }
}