package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static demo.TestUtils.lambaMatcher;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerRestController.class)
public class CustomerRestControllerTest {

 @MockBean
 private CustomerRepository customerRepository;

 @Autowired
 private MockMvc mockMvc;

 @Autowired
 private ObjectMapper objectMapper;

 private MediaType jsonContentType = MediaType
  .parseMediaType("application/json;charset=UTF-8");

 private Customer wellKnownCustomer;

 private String rootPath = "/v1/customers";

 @Before
 public void before() {
  this.wellKnownCustomer = new Customer(1L, "Bruce", "Banner");
  given(this.customerRepository.findById(this.wellKnownCustomer.getId()))
   .willReturn(Optional.of(this.wellKnownCustomer));
 }

 @Test
 public void testOptions() throws Throwable {
  this.mockMvc.perform(options(this.rootPath).accept(this.jsonContentType))
   .andExpect(status().isOk())
   .andExpect(header().string("Allow", notNullValue()));
 }

 @Test
 public void testGetCollection() throws Exception {

  List<Customer> customers = Arrays.asList(this.wellKnownCustomer,
   new Customer(this.wellKnownCustomer.getId() + 1, "A", "B"));

  given(this.customerRepository.findAll()).willReturn(customers);

  this.mockMvc
   .perform(get(this.rootPath).accept(jsonContentType))
   .andExpect(status().isOk())
   .andExpect(content().contentType(jsonContentType))
   .andExpect(
    jsonPath("$",
     hasSize(lambaMatcher("the count should be >= 1", (Integer i) -> i >= 1))));
 }

 @Test
 public void testGet() throws Exception {
  this.mockMvc
   .perform(
    get(this.rootPath + "/" + this.wellKnownCustomer.getId()).contentType(
     jsonContentType).accept(jsonContentType))
   .andExpect(status().isOk())
   .andExpect(content().contentType(jsonContentType))
   .andExpect(
    jsonPath("$.firstName", is(this.wellKnownCustomer.getFirstName())))
   .andExpect(jsonPath("$.lastName", is(this.wellKnownCustomer.getLastName())));
 }

 @Test
 public void testPost() throws Exception {
  Customer customer = new Customer("Peter", "Parker");
  Customer savedCustomer = new Customer(2L, customer.getFirstName(),
   customer.getLastName());
  given(this.customerRepository.save(customer)).willReturn(savedCustomer);
  String customerJSON = this.objectMapper.writeValueAsString(new Customer(
   customer.getFirstName(), customer.getLastName()));
  this.mockMvc
   .perform(
    post(this.rootPath).contentType(this.jsonContentType).content(customerJSON))
   .andExpect(status().isCreated())
   .andExpect(header().string("Location", notNullValue()));
 }

 @Test
 public void testDelete() throws Exception {
  this.mockMvc.perform(
   delete(this.rootPath + "/" + this.wellKnownCustomer.getId()).contentType(
    this.jsonContentType)).andExpect(status().isNoContent());
  verify(this.customerRepository).delete(this.wellKnownCustomer);
 }

 @Test
 public void testHead() throws Exception {
  this.mockMvc.perform(
   head(this.rootPath + "/" + this.wellKnownCustomer.getId()).contentType(
    this.jsonContentType)).andExpect(status().isNoContent());
 }

 @Test
 public void testPut() throws Exception {

  given(this.customerRepository.findById(this.wellKnownCustomer.getId()))
   .willReturn(Optional.of(this.wellKnownCustomer));

  String fn = "Peter", ln = "Parker";
  Customer existing = this.wellKnownCustomer;
  Customer updated = new Customer(existing.getId(), fn, ln);
  given(this.customerRepository.save(updated)).willReturn(updated);

  String content = "{ \"id\": \"" + existing.getId() + "\", \"firstName\": \""
   + fn + "\", \"lastName\": \"" + ln + "\" }";
  String idPath = this.rootPath + "/" + existing.getId();
  this.mockMvc
   .perform(put(idPath).contentType(jsonContentType).content(content))
   .andExpect(status().isCreated())
   .andExpect(header().string("Location", notNullValue()));

  given(this.customerRepository.findById(this.wellKnownCustomer.getId()))
   .willReturn(Optional.of(updated));

  this.mockMvc.perform(get(idPath)).andExpect(jsonPath("$.firstName", is(fn)))
   .andExpect(jsonPath("$.lastName", is(ln)));

 }

}
