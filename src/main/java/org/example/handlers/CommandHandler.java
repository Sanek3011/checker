package org.example.handlers;

import org.example.model.Property;
import org.example.model.Type;
import org.example.model.User;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface CommandHandler {
    List<BotApiMethod<?>> handle(Update update, User user);
    String getCommandName();

    default SendMessage getMessageCommand(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }


    default Long getChatId(Update update) {
        if (update.getCallbackQuery() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }else{
            return update.getMessage().getChatId();
        }
    }

    default SendMessage getKeyboardMessageCommand(Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage.builder()
                .chatId(chatId)
                .text("нажмите на одну из кнопок")
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
    default String formatPropertyInfo(List<Property> properties) {
        // Группируем по владельцу
        Map<String, List<Property>> byOwner = properties.stream()
                .collect(Collectors.groupingBy(Property::getOwner, HashMap::new, Collectors.toList()));

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, List<Property>> ownerEntry : byOwner.entrySet()) {
            String owner = ownerEntry.getKey();
            List<Property> ownerProps = ownerEntry.getValue();


            sb.append("Сервер: ")
                    .append(ownerProps.get(0).getKey().getServerNumber())
                    .append("\n")
                    .append(owner)
                    .append("\n");


            Map<Type, List<Property>> propertyMap = ownerProps.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getKey().getType(),
                            HashMap::new,
                            Collectors.toList()
                    ));

            for (Map.Entry<Type, List<Property>> typeEntry : propertyMap.entrySet()) {
                sb.append("\n").append(typeEntry.getKey()).append(":\n");

                List<Property> list = typeEntry.getValue();
                Type type = typeEntry.getKey();

                for (int i = 0; i < list.size(); i++) {
                    sb.append(list.get(i));

                    if (type == Type.House) {
                        if ((i + 1) % 3 == 0) {
                            sb.append("\n");
                        } else {
                            sb.append(" ");
                        }
                    } else if (type == Type.Business) {
                        sb.append("\n");
                    }
                }

                sb.append("\n");
            }

            sb.append("\n"); // разделитель между владельцами
        }

        return sb.toString();
    }

}
