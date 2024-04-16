package az.insta.business.media.management.api.telegram.command;

import az.insta.business.media.management.api.constant.CommandConstants;
import az.insta.business.media.management.api.constant.RegexConstants;
import az.insta.business.media.management.api.constant.SymbolConstants;
import az.insta.business.media.management.api.enumeration.Status;
import az.insta.business.media.management.api.repository.MediaRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.message.MaybeInaccessibleMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RejectCommand implements Command {

    public static final Pattern COMMAND_PATTERN = Pattern.compile(RegexConstants.Command.REJECT);

    private final MediaRepository mediaRepository;
    private final TelegramBot telegramBot;

    @Override
    public String getCommandPrefix() {
        return CommandConstants.REJECT;
    }

    @Override
    public boolean validate(Update update) {
        return COMMAND_PATTERN.matcher(update.callbackQuery().data()).matches();
    }

    @Override
    public void apply(Update update) {
        CallbackQuery callbackQuery = update.callbackQuery();
        MaybeInaccessibleMessage message = callbackQuery.maybeInaccessibleMessage();
        String id = callbackQuery.data().split(SymbolConstants.COLON)[1];
        log.info("Media rejected {id:{}}", id);
        mediaRepository.findById(Integer.parseInt(id))
                .ifPresent(media -> {
                    if (media.getStatus() == Status.IN_REVIEW) {
                        media.setStatus(Status.REJECTED);
                        mediaRepository.save(media);
                    }
                    var text = String.join(SymbolConstants.DOUBLE_NEW_LINE, media.getCaption(), media.getStatus().name());
                    var editMessageText = new EditMessageText(message.chat().id(), message.messageId(), text);
                    LinkPreviewOptions linkPreviewOptions = new LinkPreviewOptions();
                    linkPreviewOptions.url(media.getUrl());
                    editMessageText.linkPreviewOptions(linkPreviewOptions);
                    telegramBot.execute(editMessageText);
                });
    }

}
