package demo;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Ignore
public class HiddenHttpMethodFilterTest {

 private RestTemplate restTemplate = new RestTemplate();

 @Test
 public void testHiddenHttpMethodInvocation() throws Throwable {
  ConfigurableApplicationContext applicationContext = SpringApplication
   .run(Application.class);
  int port = AnnotationConfigEmbeddedWebApplicationContext.class
   .cast(applicationContext).getEmbeddedServletContainer().getPort();

  String s = "/v1/customers/1";
  String url = String.format("http://localhost:%d/%s", port, s);

  ResponseEntity<Map> forEntity;

  forEntity = restTemplate.getForEntity(url, Map.class);
  Assert.assertTrue(forEntity.getStatusCode().is2xxSuccessful());

  restTemplate
   .postForEntity(url + "?_method=DELETE", null, java.util.Map.class);

  try {
   forEntity = null;
   forEntity = restTemplate.getForEntity(url, Map.class);
  }
  catch (HttpClientErrorException ex) {
   // works as expected
   return;
  }
  Assert.fail();
 }
}
