package pl.calendar.interactive_calendar_backend.dto;

/**
 * Generyczna klasa opakowująca odpowiedzi z API.
 * Zapewnia spójny format odpowiedzi w całej aplikacji.
 *
 * @param <T> Typ danych zawartych w odpowiedzi.
 */

public record ApiResponse<T>( boolean success,
                           String message,
                           T data) {
}
