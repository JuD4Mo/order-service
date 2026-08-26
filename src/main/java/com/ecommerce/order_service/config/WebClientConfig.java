package com.ecommerce.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.ecommerce.order_service.service.client.IInventoryClient;

@Configuration
public class WebClientConfig {
  @Bean
  public WebClient webClientBuilder(){
    return WebClient.builder().baseUrl("http://localhost:8082").build();
  }

   @Bean
   public IInventoryClient inventoryClient(WebClient webClient){
    WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);

    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
   
    return factory.createClient(IInventoryClient.class);
  }
}
