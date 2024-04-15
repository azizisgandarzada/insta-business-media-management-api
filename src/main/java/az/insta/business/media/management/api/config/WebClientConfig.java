package az.insta.business.media.management.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Configuration
    public class InstagramGraph {

        @Value("${client.instagram-graph.url}")
        private String url;

        @Bean("instagramGraphWebClient")
        public WebClient webClient() {
            return WebClient.builder()
                    .baseUrl(url)
                    .build();
        }

    }


}
