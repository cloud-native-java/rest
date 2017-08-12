package actors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootApplication
public class DemoApplication {

 public static void main(String[] args) {
  SpringApplication.run(DemoApplication.class, args);
 }

 @Bean
 TransactionTemplate transactionTemplate(
  PlatformTransactionManager transactionManager) {
  return new TransactionTemplate(transactionManager);
 }

 @Configuration
 static class SimpleRepositoryRestMvcConfiguration extends
  RepositoryRestConfigurerAdapter {

  @Override
  public void configureRepositoryRestConfiguration(
   RepositoryRestConfiguration config) {
   config.exposeIdsFor(Movie.class, Actor.class);
  }
 }
}
