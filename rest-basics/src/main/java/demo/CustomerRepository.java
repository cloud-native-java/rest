package demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

 Collection<Customer> findByFirstNameIgnoreCase(@Param("fn") String firstName);

 Optional<Customer> findById(@Param("id") Long id);

 Collection<Customer> findByLastNameIgnoreCase(@Param("ln") String ln);
}
