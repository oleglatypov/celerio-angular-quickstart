/*
 * Project home: https://github.com/jaxio/celerio-angular-quickstart
 * 
 * Source code generated by Celerio, an Open Source code generator by Jaxio.
 * Documentation: http://www.jaxio.com/documentation/celerio/
 * Source code: https://github.com/jaxio/celerio/
 * Follow us on twitter: @jaxiosoft
 * This header can be customized in Celerio conf...
 * Template pack-angular:src/main/java/security/UserDetailsServiceImpl.java.p.vm
 */
package com.mycompany.myapp.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;

/**
 * An implementation of Spring Security's {@link UserDetailsService}.
 * 
 * @see http://static.springsource.org/spring-security/site/reference.html
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserRepository userRepository;

    @Inject
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieve an account depending on its login this method is not case sensitive.
     *
     * @param username the user's username
     * @return a Spring Security userdetails object that matches the username
     * @throws UsernameNotFoundException when the user could not be found
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Empty username");
        }
        log.debug("Security verification for user '{}'", username);

        User account = userRepository.getByLogin(username);

        if (account == null) {
            log.info("User {} could not be found", username);
            throw new UsernameNotFoundException("user " + username + " could not be found");
        }

        Collection<GrantedAuthority> grantedAuthorities = toGrantedAuthorities(account.getRoleNames());
        String password = account.getPassword();
        boolean enabled = account.getIsEnabled();
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        return new UserWithId(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuthorities, account.getId());
    }

    private Collection<GrantedAuthority> toGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> result = new ArrayList();
        for (String role : roles) {
            result.add(new SimpleGrantedAuthority(role));
        }
        return result;
    }
}