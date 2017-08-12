package demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import java.util.Arrays;

@SpringBootApplication
public class Application {

 public static void main(String[] args) {
  SpringApplication.run(Application.class, args);
 }

 @ControllerAdvice
 public static class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {

  public JsonpAdvice() {
   super("callback");
  }
 }

 @Bean
 CommandLineRunner init(CustomerRepository r) {
  return args -> Arrays
   .stream(
    ("Mark,Fisher;Scott,Frederick;Brian,Dussault;"
     + "Josh,Long;Kenny,Bastani;Dave,Syer;Spencer,Gibb").split(";"))
   .map(n -> n.split(",")).map(tpl -> r.save(new Customer(tpl[0], tpl[1])))
   .forEach(System.out::println);
 }
}
