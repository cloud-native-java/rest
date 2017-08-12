package demo;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@ControllerAdvice(annotations = RestController.class)
public class CustomerControllerAdvice {

 // <1>
 private final MediaType vndErrorMediaType = MediaType
  .parseMediaType("application/vnd.error");

 // <2>
 @ExceptionHandler(CustomerNotFoundException.class)
 ResponseEntity<VndErrors> notFoundException(CustomerNotFoundException e) {
  return this.error(e, HttpStatus.NOT_FOUND, e.getCustomerId() + "");
 }

 @ExceptionHandler(IllegalArgumentException.class)
 ResponseEntity<VndErrors> assertionException(IllegalArgumentException ex) {
  return this.error(ex, HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
 }

 // <3>
 private <E extends Exception> ResponseEntity<VndErrors> error(E error,
  HttpStatus httpStatus, String logref) {
  String msg = Optional.of(error.getMessage()).orElse(
   error.getClass().getSimpleName());
  HttpHeaders httpHeaders = new HttpHeaders();
  httpHeaders.setContentType(this.vndErrorMediaType);
  return new ResponseEntity<>(new VndErrors(logref, msg), httpHeaders,
   httpStatus);
 }
}