package actors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TraversonTest {

 private Log log = LogFactory.getLog(getClass());

 private ConfigurableApplicationContext server;

 private Traverson traverson;

 @Before
 public void setUp() throws Exception {

  this.server = new SpringApplicationBuilder()
   .properties(Collections.singletonMap("server.port", "0"))
   .sources(DemoApplication.class).run();
  // this.server =
  // SpringApplication.run(DemoApplication.class);
  this.traverson = this.server.getBean(Traverson.class);
 }

 @After
 public void tearDown() throws Exception {
  if (null != this.server) {
   this.server.close();
  }
 }

 @Test
 public void testTraverson() throws Exception {

  String nameOfMovie = "Cars";

  // <1>
  Resources<Actor> actorResources = this.traverson
   .follow("actors", "search", "by-movie")
   .withTemplateParameters(Collections.singletonMap("movie", nameOfMovie))
   .toObject(new ParameterizedTypeReference<Resources<Actor>>() {
   });

  actorResources.forEach(this.log::info);
  assertTrue(actorResources.getContent().size() > 0);
  assertEquals(
   actorResources.getContent().stream()
    .filter(actor -> actor.fullName.equals("Owen Wilson"))
    .collect(Collectors.toList()).size(), 1);
 }
}
