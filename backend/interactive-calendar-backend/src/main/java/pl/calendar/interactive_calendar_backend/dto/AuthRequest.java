package pl.calendar.interactive_calendar_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest( @NotBlank(message = "Email nie może być pusty")
                           @Email(message = "Niepoprawny format adresu email")
                           String email,

                           @NotBlank(message = "Hasło nie może być puste")
                           String password) {
}
