package pl.calendar.interactive_calendar_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.calendar.interactive_calendar_backend.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Znajdujemy użytkownika w naszej bazie danych po emailu
        return userRepository.findByEmail(email)
                .map(user -> new User(
                        user.getEmail(),
                        user.getPassword(),
                        // Mapujemy rolę z naszej bazy na "GrantedAuthority" zrozumiałe dla Springa
                        Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
                ))
                // Jeśli użytkownik nie zostanie znaleziony, rzucamy wyjątek
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
