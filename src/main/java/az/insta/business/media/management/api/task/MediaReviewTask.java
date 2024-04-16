package az.insta.business.media.management.api.task;

import static az.insta.business.media.management.api.constant.CommandConstants.APPROVE;
import static az.insta.business.media.management.api.constant.CommandConstants.REJECT;
import static az.insta.business.media.management.api.constant.SymbolConstants.CHECK_MARK_BUTTON;
import static az.insta.business.media.management.api.constant.SymbolConstants.CROSS_MARK;

import az.insta.business.media.management.api.constant.SymbolConstants;
import az.insta.business.media.management.api.enumeration.Status;
import az.insta.business.media.management.api.repository.MediaRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaReviewTask {

    private final MediaRepository mediaRepository;
    private final TelegramBot telegramBot;

    @Value("${client.telegram-tk-bot.credentials.allowed-chat-id}")
    private Long allowedChatId;

    @Scheduled(cron = "*/5 * 10-23 * * *", zone = "Asia/Baku")
    public void run() {
        mediaRepository.findAllByStatus(Status.CREATED)
                .forEach(media -> {
                    var sendMessage = new SendMessage(allowedChatId, media.getCaption());
                    var inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    var approve = new InlineKeyboardButton(String.join(SymbolConstants.SPACE, "Approve", CHECK_MARK_BUTTON));
                    approve.callbackData(String.join(SymbolConstants.COLON, APPROVE,
                            media.getId().toString()));
                    inlineKeyboardMarkup.addRow(approve);
                    var reject = new InlineKeyboardButton(String.join(SymbolConstants.SPACE, "Reject", CROSS_MARK));
                    reject.callbackData(String.join(SymbolConstants.COLON, REJECT, media.getId().toString()));
                    inlineKeyboardMarkup.addRow(reject);
                    sendMessage.replyMarkup(inlineKeyboardMarkup);
                    var linkPreviewOptions = new LinkPreviewOptions();
                    linkPreviewOptions.url(media.getUrl());
                    sendMessage.linkPreviewOptions(linkPreviewOptions);
                    SendResponse response = telegramBot.execute(sendMessage);
                    if (response.isOk()) {
                        media.setStatus(Status.IN_REVIEW);
                        mediaRepository.save(media);
                        log.info("Media sent to review {id:{}, type:{}}", media.getId(), media.getType());
                    }
                });
    }

}
