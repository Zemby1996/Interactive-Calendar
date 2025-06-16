package pl.calendar.interactive_calendar_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.calendar.interactive_calendar_backend.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Znajduje użytkownika w bazie danych na podstawie jego adresu email.
     * Zwraca Optional, który może zawierać użytkownika lub być pusty, jeśli użytkownik nie zostanie znaleziony.
     * @param email adres email do wyszukania.
     * @return Optional zawierający znalezionego użytkownika lub pusty.
     */
    Optional<User> findByEmail(String email);
}
