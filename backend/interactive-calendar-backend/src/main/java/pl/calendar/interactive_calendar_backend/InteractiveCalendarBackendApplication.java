package pl.calendar.interactive_calendar_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InteractiveCalendarBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteractiveCalendarBackendApplication.class, args);
	}

}
