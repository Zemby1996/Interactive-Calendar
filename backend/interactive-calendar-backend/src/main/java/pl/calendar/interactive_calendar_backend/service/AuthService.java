package pl.calendar.interactive_calendar_backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.calendar.interactive_calendar_backend.dto.AuthRequest;
import pl.calendar.interactive_calendar_backend.dto.RegisterRequest;
import pl.calendar.interactive_calendar_backend.model.User;
import pl.calendar.interactive_calendar_backend.repository.UserRepository;
import pl.calendar.interactive_calendar_backend.security.CustomUserDetailsService;
import pl.calendar.interactive_calendar_backend.util.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Rejestruje nowego użytkownika w systemie.
     * @param request Dane rejestracyjne (email, hasło, imię, nazwisko).
     */

    public void register(RegisterRequest request){
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("User with email " + request.email() + " already exists!");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    /**
     * Loguje użytkownika i zwraca token JWT.
     * @param request Dane logowania (email, hasło).
     * @return Token JWT.
     */

    public String login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        return jwtUtil.generateToken(userDetails);
    }
}
