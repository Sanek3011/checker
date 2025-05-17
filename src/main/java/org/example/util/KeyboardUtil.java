package org.example.util;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@UtilityClass
public class KeyboardUtil {

    private static final List<List<InlineKeyboardButton>> MENU_BUTTONS = List.of(
            List.of(createButton("Информация об аукционах", "getAuctionInfo")),
            List.of(createButton("По владельцу", "findByOwner"),
                    createButton("По промежутку", "findByInterval")),
            List.of(createButton("По кол-ву", "findByQuantity")));

    public static InlineKeyboardButton createButton(String text, String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callback);
        return button;

    }

    public static InlineKeyboardMarkup getMenuKeyboard() {
        return InlineKeyboardMarkup.builder()
                .keyboard(MENU_BUTTONS)
                .build();

    }
}
