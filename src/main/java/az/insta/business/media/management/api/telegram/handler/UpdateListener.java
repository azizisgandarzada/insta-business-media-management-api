package az.insta.business.media.management.api.telegram.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateListener {

    private final TelegramBot telegramBot;
    private final UpdateHandler updateHandler;

   // @PostConstruct
    public void start() {
        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                try {
                    updateHandler.handle(update);
                } catch (Exception ex) {
                    log.error("Failed to listen update {ex:{}}", ex.getMessage());
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, ex -> log.error("Failed to listen update {ex:{}}", ex.getMessage()));
    }

}
