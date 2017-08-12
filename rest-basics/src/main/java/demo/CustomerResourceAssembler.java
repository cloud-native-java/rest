package demo;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation
        .MvcUriComponentsBuilder;

import java.net.URI;

@Component
class CustomerResourceAssembler implements
 ResourceAssembler<Customer, Resource<Customer>> {

 @Override
 public Resource<Customer> toResource(Customer customer) {

  Resource<Customer> customerResource = new Resource<>(customer);//<1>
  URI photoUri = MvcUriComponentsBuilder
   .fromMethodCall(
    MvcUriComponentsBuilder.on(CustomerProfilePhotoRestController.class).read(
     customer.getId())).buildAndExpand().toUri();

  URI selfUri = MvcUriComponentsBuilder
   .fromMethodCall(
    MvcUriComponentsBuilder.on(CustomerHypermediaRestController.class).get(
     customer.getId())).buildAndExpand().toUri();

  customerResource.add(new Link(selfUri.toString(), "self"));
  customerResource.add(new Link(photoUri.toString(), "profile-photo"));
  return customerResource;
 }
}
