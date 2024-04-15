package az.insta.business.media.management.api.telegram.command;

import com.pengrad.telegrambot.model.Update;

public interface Command {

    String getCommandPrefix();

    boolean validate(Update update);

    void apply(Update update);

}
