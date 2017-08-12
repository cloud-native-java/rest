package actors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Actor {

 @Id
 @GeneratedValue
 public Long id;

 @ManyToOne
 public Movie movie;

 public String fullName;

 public Actor(String n, Movie movie) {
  this.fullName = n;
  this.movie = movie;
 }

 Actor() {
 }

 @Override
 public String toString() {
  return "Actor{" + "id=" + id + ", fullName='" + this.fullName + '\'' + '}';
 }
}
