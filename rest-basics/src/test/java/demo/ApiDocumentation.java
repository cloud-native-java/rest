package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.RequestDispatcher;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//@formatter:off
// <1>
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class ApiDocumentation {
//@formatter:off

 // <1>
 // @Rule public final RestDocumentation restDocumentation =
 //   new RestDocumentation(
 //  "target/generated-snippets");

 @Autowired
 private MockMvc mockMvc;

 @Test
 public void errorExample() throws Exception {
  this.mockMvc
   .perform(
    get("/error")
     .contentType(MediaType.APPLICATION_JSON)
     .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, 400)
     .requestAttr(RequestDispatcher.ERROR_REQUEST_URI, "/customers")
     .requestAttr(RequestDispatcher.ERROR_MESSAGE,
      "The customer 'http://localhost:8443/v1/customers/123' does not exist"))
   .andDo(print()).andExpect(status().isBadRequest())
   .andExpect(jsonPath("error", is("Bad Request")))
   .andExpect(jsonPath("timestamp", is(notNullValue())))
   .andExpect(jsonPath("status", is(400)))
   .andExpect(jsonPath("path", is(notNullValue())))
   .andDo(document("error-example")); // <3>
 }

 @Test
 public void indexExample() throws Exception {
  this.mockMvc.perform(get("/v1/customers")).andExpect(status().isOk())
   .andDo(document("index-example"));
 }
}
