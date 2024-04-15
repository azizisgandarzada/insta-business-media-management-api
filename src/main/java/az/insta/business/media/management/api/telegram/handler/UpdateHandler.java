package az.insta.business.media.management.api.telegram.handler;

import static az.insta.business.media.management.api.constant.SymbolConstants.COLON;

import az.insta.business.media.management.api.telegram.command.Command;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateHandler {

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    private final ApplicationContext applicationContext;

    @Value("${client.telegram-tk-bot.credentials.allowed-chat-id}")
    private Long allowedChatId;

    @PostConstruct
    public void initCommands() {
        applicationContext.getBeansOfType(Command.class)
                .forEach((key, value) -> COMMANDS.put(value.getCommandPrefix(), value));
    }

    public void handle(Update update) {
        if (update.callbackQuery() == null) {
            return;
        }
        if (!allowedChatId.equals(update.callbackQuery().maybeInaccessibleMessage().chat().id())) {
            return;
        }
        String commandPrefix = update.callbackQuery().data().split(COLON)[0];
        Command command = COMMANDS.get(commandPrefix);
        if (command == null || !command.validate(update)) {
            return;
        }
        command.apply(update);
    }

}
