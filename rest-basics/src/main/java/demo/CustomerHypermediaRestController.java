package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@formatter:off
import org.springframework.web.servlet.mvc.method
        .annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support
        .ServletUriComponentsBuilder;
//@formatter:on

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// <1>
@RestController
@RequestMapping(value = "/v2", produces = "application/hal+json")
public class CustomerHypermediaRestController {

 private final CustomerResourceAssembler customerResourceAssembler; // <2>

 private final CustomerRepository customerRepository;

 @Autowired
 CustomerHypermediaRestController(CustomerResourceAssembler cra,
                                  CustomerRepository customerRepository) {
  this.customerRepository = customerRepository;
  this.customerResourceAssembler = cra;
 }

 // <3>
 @GetMapping
 ResponseEntity<Resources<Object>> root() {
  Resources<Object> objects = new Resources<>(Collections.emptyList());
  URI uri = MvcUriComponentsBuilder
   .fromMethodCall(MvcUriComponentsBuilder.on(getClass()).getCollection())
   .build().toUri();
  Link link = new Link(uri.toString(), "customers");
  objects.add(link);
  return ResponseEntity.ok(objects);
 }

 // <4>
 @GetMapping("/customers")
 ResponseEntity<Resources<Resource<Customer>>> getCollection() {
  List<Resource<Customer>> collect = this.customerRepository.findAll().stream()
   .map(customerResourceAssembler::toResource)
   .collect(Collectors.<Resource<Customer>>toList());
  Resources<Resource<Customer>> resources = new Resources<>(collect);
  URI self = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
  resources.add(new Link(self.toString(), "self"));
  return ResponseEntity.ok(resources);
 }

 @RequestMapping(value = "/customers", method = RequestMethod.OPTIONS)
 ResponseEntity<?> options() {
  return ResponseEntity
   .ok()
   .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS,
    HttpMethod.PUT, HttpMethod.DELETE).build();
 }

 @GetMapping(value = "/customers/{id}")
 ResponseEntity<Resource<Customer>> get(@PathVariable Long id) {
  return this.customerRepository.findById(id)
   .map(c -> ResponseEntity.ok(this.customerResourceAssembler.toResource(c)))
   .orElseThrow(() -> new CustomerNotFoundException(id));
 }

 @PostMapping(value = "/customers")
 ResponseEntity<Resource<Customer>> post(@RequestBody Customer c) {
  Customer customer = this.customerRepository.save(new Customer(c
   .getFirstName(), c.getLastName()));
  URI uri = MvcUriComponentsBuilder.fromController(getClass())
   .path("/customers/{id}").buildAndExpand(customer.getId()).toUri();
  return ResponseEntity.created(uri).body(
   this.customerResourceAssembler.toResource(customer));
 }

 @DeleteMapping(value = "/customers/{id}")
 ResponseEntity<?> delete(@PathVariable Long id) {
  return this.customerRepository.findById(id).map(c -> {
   customerRepository.delete(c);
   return ResponseEntity.noContent().build();
  }).orElseThrow(() -> new CustomerNotFoundException(id));
 }

 @RequestMapping(value = "/customers/{id}", method = RequestMethod.HEAD)
 ResponseEntity<?> head(@PathVariable Long id) {
  return this.customerRepository.findById(id)
   .map(exists -> ResponseEntity.noContent().build())
   .orElseThrow(() -> new CustomerNotFoundException(id));
 }

 @PutMapping("/customers/{id}")
 ResponseEntity<Resource<Customer>> put(@PathVariable Long id,
  @RequestBody Customer c) {
  Customer customer = this.customerRepository.save(new Customer(id, c
   .getFirstName(), c.getLastName()));
  Resource<Customer> customerResource = this.customerResourceAssembler
   .toResource(customer);
  URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest()
   .toUriString());
  return ResponseEntity.created(selfLink).body(customerResource);
 }
}
