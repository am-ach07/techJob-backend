
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class EnvDebug {

	@Value("${spring.datasource.url}")
    private String url;

	@Value("${spring.datasource.password}")
    private String pswd;

    @Value("${spring.datasource.username}")
    private String user;

    

    @PostConstruct
    public void printEnv() {
        System.out.println("=== RAILWAY DB ENV VALUES ===");
        System.out.println("MYSQL_URL = " + url);
        System.out.println("MYSQLPASSWORD = " + pswd);
        System.out.println("MYSQLUSER = " + user);
        System.out.println("==============================");
    }
}