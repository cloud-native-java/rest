package demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

@Configuration
class GoogleProtocolBuffersConfiguration {

 @Bean
 ProtobufHttpMessageConverter protobufHttpMessageConverter() {
  return new ProtobufHttpMessageConverter();
 }
}
