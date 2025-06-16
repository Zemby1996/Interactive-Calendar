package pl.calendar.interactive_calendar_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest( @NotBlank(message = "Email nie może być pusty")
                               @Email(message = "Niepoprawny format adresu email")
                               String email,

                               @NotBlank(message = "Hasło nie może być puste")
                               @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
                               String password,

                               @NotBlank(message = "Imię nie może być puste")
                               String firstName,

                               @NotBlank(message = "Nazwisko nie może być puste")
                               String lastName) {
}
