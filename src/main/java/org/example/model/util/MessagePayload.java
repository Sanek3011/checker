package org.example.model.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
@Builder
@AllArgsConstructor


public class MessagePayload {
    private final Long chatId;
    private final String text;
    private final InlineKeyboardMarkup keyboard;
    private final boolean fallbackToFile;
    private final boolean sendAsFile;


}
