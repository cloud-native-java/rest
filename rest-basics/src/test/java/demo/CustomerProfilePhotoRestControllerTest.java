package demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CustomerProfilePhotoRestControllerTest {

 private static Log log = LogFactory
  .getLog(CustomerProfilePhotoRestControllerTest.class);

 private static File tmpFile = new File(System.getProperty("java.io.tmpdir"),
  "images/" + Long.toString(System.currentTimeMillis()));

 @Autowired
 private CustomerRepository customerRepository;

 @Autowired
 private MockMvc mockMvc;

 private Customer bruceBanner, peterParker;

 private byte[] dogeBytes;

 private String urlTemplate = "/customers/{id}/photo";

 private MediaType vndErrorMediaType = MediaType
  .parseMediaType("application/vnd.error");

 @AfterClass
 public static void after() throws Throwable {
  if (tmpFile.exists()) {
   Files.walkFileTree(tmpFile.toPath(), new SimpleFileVisitor<Path>() {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
     throws IOException {
     Files.delete(file);
     return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
     throws IOException {
     Files.delete(dir);
     return FileVisitResult.CONTINUE;
    }
   });
  }
 }

 @Before
 public void before() throws Throwable {

  Resource dogeResource = new ClassPathResource("doge.jpg");
  dogeBytes = StreamUtils.copyToByteArray(dogeResource.getInputStream());
  Assert.assertTrue(dogeResource.contentLength() > 0);
  Assert.assertTrue(dogeResource.exists());

  this.bruceBanner = this.customerRepository.findById(1L).orElseGet(
   () -> this.customerRepository.save(new Customer("Bruce", "Banner")));

  this.peterParker = this.customerRepository.findById(2L).orElseGet(
   () -> this.customerRepository.save(new Customer("Peter", "Parker")));

 }

 @Test
 public void nonBlockingPhotoUploadWithExistingCustomer() throws Exception {

  MvcResult mvcResult = this.mockMvc
   .perform(
    fileUpload(this.urlTemplate, this.bruceBanner.getId()).file("file",
     this.dogeBytes)).andExpect(request().asyncStarted()).andReturn();

  mvcResult.getAsyncResult();

  MvcResult mvcResultWithLocation = this.mockMvc
   .perform(asyncDispatch(mvcResult)).andExpect(status().isCreated())
   .andExpect(header().string("Location", notNullValue())).andReturn();
  String location = mvcResultWithLocation.getResponse().getHeader("Location");
  Assert.assertEquals(location, "http://localhost/customers/"
   + this.bruceBanner.getId() + "/photo");
  log.info("location: " + location);
 }

 @Test
 public void nonBlockingPhotoUploadWithNonExistingCustomer() throws Exception {

  MvcResult mvcResult = this.mockMvc
   .perform(fileUpload(this.urlTemplate, 0).file("file", this.dogeBytes))
   .andExpect(request().asyncStarted()).andReturn();

  mvcResult.getAsyncResult();

  this.mockMvc
   .perform(asyncDispatch(mvcResult))
   .andExpect(status().isNotFound())
   .andExpect(
    header()
     .string(HttpHeaders.CONTENT_TYPE, this.vndErrorMediaType.toString()))
   .andReturn();
 }

 @Test
 public void photoDownloadWithExistingPhoto() throws Exception {
  MvcResult mvcResult = this.mockMvc
   .perform(
    fileUpload(this.urlTemplate, this.bruceBanner.getId()).file("file",
     this.dogeBytes)).andExpect(request().asyncStarted()).andReturn();

  mvcResult.getAsyncResult();

  this.mockMvc
   .perform(
    get(this.urlTemplate, this.bruceBanner.getId())
     .accept(MediaType.IMAGE_JPEG))
   .andExpect(
    header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
   .andExpect(status().isOk());
 }

 @Test
 public void photoDownloadWithNonExistingPhoto() throws Exception {
  MvcResult mvcResult = this.mockMvc
   .perform(get(this.urlTemplate, this.peterParker.getId()))
   .andExpect(status().isNotFound())
   .andExpect(
    header()
     .string(HttpHeaders.CONTENT_TYPE, this.vndErrorMediaType.toString()))
   .andReturn();

  log.info(mvcResult.getResponse().getContentAsString());

  this.mockMvc
   .perform(get(this.urlTemplate, 0))
   .andExpect(status().isNotFound())
   .andExpect(
    header()
     .string(HttpHeaders.CONTENT_TYPE, this.vndErrorMediaType.toString()))
   .andReturn();
 }

 @Configuration
 public static class EnvironmentConfiguration {

  @Autowired
  public void configureEnvironment(
   ConfigurableWebApplicationContext webApplicationContext) throws Exception {

   PropertySource<Object> propertySource = new PropertySource<Object>("uploads") {

    @Override
    public Object getProperty(String name) {
     if (name.equals("upload.dir")) {
      return tmpFile.getAbsolutePath();
     }
     return null;
    }
   };

   webApplicationContext.getEnvironment().getPropertySources()
    .addLast(propertySource);
  }
 }

}
