package actors;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestTemplateTest {

 private Log log = LogFactory.getLog(getClass());

 private URI baseUri;

 private ConfigurableApplicationContext server;

 private RestTemplate restTemplate;

 private MovieRepository movieRepository;

 private URI moviesUri;

 @Before
 public void setUp() throws Exception {

  this.server = new SpringApplicationBuilder()
   .properties(Collections.singletonMap("server.port", "0"))
   .sources(DemoApplication.class).run();

  int port = this.server.getEnvironment().getProperty("local.server.port",
   Integer.class, 8080);

  this.restTemplate = this.server.getBean(RestTemplate.class);
  this.baseUri = URI.create("http://localhost:" + port + "/");
  this.moviesUri = URI.create(this.baseUri.toString() + "movies");
  this.movieRepository = this.server.getBean(MovieRepository.class);
 }

 @After
 public void tearDown() throws Exception {
  if (null != this.server) {
   this.server.close();
  }
 }

 @Test
 public void testRestTemplate() throws Exception {
  // <1>
  ResponseEntity<Movie> postMovieResponseEntity = this.restTemplate
   .postForEntity(moviesUri, new Movie("Forest Gump"), Movie.class);
  URI uriOfNewMovie = postMovieResponseEntity.getHeaders().getLocation();
  log.info("the new movie lives at " + uriOfNewMovie);

  // <2>
  JsonNode mapForMovieRecord = this.restTemplate.getForObject(uriOfNewMovie,
   JsonNode.class);
  log.info("\t..read as a Map.class: " + mapForMovieRecord);
  assertEquals(mapForMovieRecord.get("title").asText(),
   postMovieResponseEntity.getBody().title);

  // <3>
  Movie movieReference = this.restTemplate.getForObject(uriOfNewMovie,
   Movie.class);
  assertEquals(movieReference.title, postMovieResponseEntity.getBody().title);
  log.info("\t..read as a Movie.class: " + movieReference);

  // <4>
  ResponseEntity<Movie> movieResponseEntity = this.restTemplate.getForEntity(
   uriOfNewMovie, Movie.class);
  assertEquals(movieResponseEntity.getStatusCode(), HttpStatus.OK);
  assertEquals(movieResponseEntity.getHeaders().getContentType(),
   MediaType.parseMediaType("application/json;charset=UTF-8"));
  log.info("\t..read as a ResponseEntity<Movie>: " + movieResponseEntity);

  // <5>

  //@formatter:off
  ParameterizedTypeReference<Resources<Movie>> movies =
          new ParameterizedTypeReference<Resources<Movie>>() {};
  //@formatter:on
  ResponseEntity<Resources<Movie>> moviesResponseEntity = this.restTemplate
   .exchange(this.moviesUri, HttpMethod.GET, null, movies);
  Resources<Movie> movieResources = moviesResponseEntity.getBody();
  movieResources.forEach(this.log::info);
  assertEquals(movieResources.getContent().size(), this.movieRepository.count());
  assertTrue(movieResources.getLinks().stream()
   .filter(m -> m.getRel().equals("self")).count() == 1);

 }
}
