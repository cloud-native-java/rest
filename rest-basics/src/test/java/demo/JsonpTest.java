package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static demo.TestUtils.lambaMatcher;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CustomerRestController.class)
public class JsonpTest {

 @MockBean
 private CustomerRepository customerRepository;

 @Autowired
 private MockMvc mockMvc;

 @Test
 public void testJsonpCallbacks() throws Throwable {

  given(this.customerRepository.findAll()).willReturn(
   Arrays.asList(new Customer(1L, "A1", "B1"), new Customer(2L, "A2", "B2")));

  String callbackName = "callMeMaybe";
  this.mockMvc
   .perform(get("/v1/customers?callback=" + callbackName))
   .andExpect(status().isOk())
   .andExpect(
    content().string(
     lambaMatcher("the result should contain the JavaScript"
      + " invocation syntax if it's a JSONP request",
      (String result) -> result.startsWith("/**/" + callbackName + "("))));
 }
}
