import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AllEnvPrinter implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("====== ALL ENV VARIABLES START ======");
        System.getenv().forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
        System.out.println("====== ALL ENV VARIABLES END ======");
    }
}