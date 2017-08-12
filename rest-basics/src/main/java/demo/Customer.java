package demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Customer {

 @Id
 @GeneratedValue
 private Long id;

 private String firstName, lastName;

 public Customer(Long id, String f, String l) {
  this.id = id;
  this.firstName = f;
  this.lastName = l;
 }

 public Customer() {
 }

 public Customer(String firstName, String lastName) {
  this.firstName = firstName;
  this.lastName = lastName;
 }

 @Override
 public boolean equals(Object o) {
  if (this == o)
   return true;
  if (!(o instanceof Customer))
   return false;
  Customer customer = (Customer) o;
  return Objects.equals(id, customer.id)
   && Objects.equals(firstName, customer.firstName)
   && Objects.equals(lastName, customer.lastName);
 }

 @Override
 public int hashCode() {
  return Objects.hash(id, firstName, lastName);
 }

 @Override
 public String toString() {
  return "Customer{" + "id=" + id + ", firstName='" + firstName + '\''
   + ", lastName='" + lastName + '\'' + '}';
 }

 public Long getId() {
  return id;
 }

 public String getFirstName() {
  return firstName;
 }

 public String getLastName() {
  return lastName;
 }
}
