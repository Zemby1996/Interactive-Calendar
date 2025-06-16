package pl.calendar.interactive_calendar_backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.calendar.interactive_calendar_backend.model.Appointment;

@Service
@RequiredArgsConstructor

public class EmailService {

    private final JavaMailSender javaMailSender;

    /**
     * Wysyła prostą wiadomość email.
     * @param to Adres odbiorcy.
     * @param subject Temat wiadomości.a
     * @param text Treść wiadomości.
     */

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch ( Exception e ) {
            System.err.println("Błąd podczas wysyłania e-maila: " + e.getMessage());
        }
    }
    /**
     * Wysyła powiadomienie o anulowaniu wizyty do pacjenta.
     */
    public void sendCancellationNotification(Appointment appointment) {
        String to = appointment.getPatient().getEmail();
        String subject = "Anulowanie wizyty - Potwierdzenie";
        String text = String.format(
                "Witaj %s,\n\nTwoja wizyta u %s zaplanowana na %s o godzinie %s została pomyślnie anulowana.\n\nPozdrawiamy,\nTwoja Przychodnia",
                appointment.getPatient().getFirstName(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentStart().toLocalDate(),
                appointment.getAppointmentStart().toLocalTime()
        );
        sendEmail(to, subject, text);
    }

    /**
     * Wysyła powiadomienie o zmianie terminu wizyty do pacjenta.
     */
    public void sendUpdateNotification(Appointment appointment) {
        String to = appointment.getPatient().getEmail();
        String subject = "Aktualizacja rezerwacji wizyty";
        String text = String.format(
                "Witaj %s,\n\nTwoja wizyta u %s została zaktualizowana. Nowy termin to: %s, godzina %s.\n\nPozdrawiamy,\nTwoja Przychodnia",
                appointment.getPatient().getFirstName(),
                appointment.getDoctor().getName(),
                appointment.getAppointmentStart().toLocalDate(),
                appointment.getAppointmentStart().toLocalTime()
        );
        sendEmail(to, subject, text);
    }
}
