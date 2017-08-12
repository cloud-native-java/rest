package actors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Movie {

 @Id
 @GeneratedValue
 public Long id;

 public String title;

 @OneToMany
 public Set<Actor> actors = new HashSet<>();

 public Movie(String title) {
  this.title = title;
 }

 Movie() {
 }

 @Override
 public String toString() {
  return "Movie{" + "id=" + id + ", title='" + title + '\'' + ", actors="
   + actors + '}';
 }
}
