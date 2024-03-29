package ru.kata.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {


    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("User '%s' not found", username));
        }

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getAuthorities());

    }


    @Override
    public User getUserById(int id) {
        Optional<User> userById = userRepository.findById(id);
        return userById.get();

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User saveUser(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }


    @Transactional
    @Override
    public void removeUserById(int id) {
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public User updateUser(int id, User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername() != null ? user.getUsername() : existingUser.getUsername());
            existingUser.setFull_name(user.getFull_name() != null ? user.getFull_name() : existingUser.getFull_name());
            existingUser.setPhone_number(user.getPhone_number() != null ? user.getPhone_number() :
                    existingUser.getPhone_number());
            existingUser.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());
            existingUser.setPassword(new BCryptPasswordEncoder().encode((user.getPassword() != null ? user.getPassword() : existingUser.getPassword())));
            return userRepository.save(existingUser);
        }
        throw new EntityNotFoundException("User with id " + id + " not found");
    }

}
