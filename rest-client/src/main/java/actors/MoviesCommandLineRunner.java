package actors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MoviesCommandLineRunner implements CommandLineRunner {

 private final TransactionTemplate transactionTemplate;

 private final ActorRepository actorRepository;

 private final MovieRepository movieRepository;

 @Autowired
 public MoviesCommandLineRunner(TransactionTemplate transactionTemplate,
  ActorRepository actorRepository, MovieRepository movieRepository) {
  this.transactionTemplate = transactionTemplate;
  this.actorRepository = actorRepository;
  this.movieRepository = movieRepository;
 }

 @Override
 public void run(String... strings) throws Exception {

  this.transactionTemplate.execute(tx -> Stream
   .of("Cars (Owen Wilson,Paul Newman,Bonnie Hunt)",
    "Batman (Michael Keaton,Jack Nicholson)",
    "Lost in Translation (Bill Murray)").map(String::trim).map(i -> {
    Matcher matcher = Pattern.compile("(.*?)\\s*?\\((.*?)\\)").matcher(i);
    Assert.state(matcher.matches());
    Movie movie = movieRepository.save(new Movie(matcher.group(1)));
    Arrays.stream(matcher.group(2).split(",")).map(String::trim).forEach(a -> {
     Actor actor = actorRepository.save(new Actor(a.trim(), movie));
     movie.actors.add(actorRepository.findOne(actor.id));
     movieRepository.save(movie);
    });
    return movieRepository.findOne(movie.id);
   }).collect(Collectors.toList()));
 }

}
