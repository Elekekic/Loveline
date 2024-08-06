package backend.Loveline_backend.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class AppConfig {

    @Bean
    public Cloudinary getCloudinary(@Value("${cloudinary.name}") String name,
                                    @Value("${cloudinary.apikey}") String apikey,
                                    @Value("${cloudinary.secret}") String secret) {

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", name);
        config.put("api_key", apikey);
        config.put("api_secret", secret);
        return new Cloudinary(config);
    }


    @Bean
    public JavaMailSenderImpl getJavaMailSender(@Value("${gmail.mail.transport.protocol}") String protocol,
                                                @Value("${gmail.mail.smtp.auth}") String auth,
                                                @Value("${gmail.mail.smtp.starttls.enable}") String starttls,
                                                @Value("${gmail.mail.debug}") String debug,
                                                @Value("${gmail.mail.from}") String from,
                                                @Value("${gmail.mail.from.password}") String password,
                                                @Value("${gmail.smtp.ssl.enable}") String ssl,
                                                @Value("${gmail.smtp.host}") String host,
                                                @Value("${gmail.smtp.port}") String port) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));

        mailSender.setUsername(from);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", debug);
        props.put("smtp.ssl.enable", ssl);

        return mailSender;

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
