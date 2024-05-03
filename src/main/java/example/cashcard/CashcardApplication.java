package example.cashcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings({"checkstyle:finalclass", "checkstyle:hideutilityclassconstructor"})
public class CashcardApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CashcardApplication.class, args);
    }

}
