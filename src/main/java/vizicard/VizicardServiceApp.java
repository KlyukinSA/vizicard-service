package vizicard;

import java.util.ArrayList;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import vizicard.model.Profile;
import vizicard.model.AppUserRole;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import vizicard.service.UserService;

@SpringBootApplication
@RequiredArgsConstructor
public class VizicardServiceApp implements CommandLineRunner {

  final UserService userService;

  public static void main(String[] args) {
    SpringApplication.run(VizicardServiceApp.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Override
  public void run(String... params) throws Exception {
    Profile client = new Profile();
    client.setUsername("client");
    client.setPassword("client");
    client.setName("cl name");

    userService.signup(client);
  }

}
