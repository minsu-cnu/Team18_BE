package team18.team18_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class Team18BeApplication {

  public static void main(String[] args) {
    SpringApplication.run(Team18BeApplication.class, args);
  }

}
