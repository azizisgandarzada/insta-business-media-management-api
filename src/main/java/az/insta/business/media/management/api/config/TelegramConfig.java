package az.insta.business.media.management.api.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    @Value("${client.telegram-tk-bot.credentials.token}")
    private String token;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(token);
    }

}
