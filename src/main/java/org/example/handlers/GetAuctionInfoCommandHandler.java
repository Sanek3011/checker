package org.example.handlers;

import lombok.RequiredArgsConstructor;
import org.example.model.Property;
import org.example.model.State;
import org.example.model.User;
import org.example.model.util.MessagePayload;
import org.example.service.PropertyService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
@Component
@RequiredArgsConstructor
public class GetAuctionInfoCommandHandler implements CommandHandler{
    private final PropertyService propertyService;
    private final UserService userService;

    @Override
    public List<MessagePayload> handle(Update update, User user) {
        Long chatId = getChatId(update);
        try {
            switch (user.getState()) {
                case NO:
                    userService.updateUserState(chatId, State.WAITING_SERVER);
                    return List.of(new MessagePayload(chatId, "Введите номер сервера", null, false, false));
                case WAITING_SERVER:
                    Integer server = Integer.parseInt(update.getMessage().getText());
                    if (server < 1 || server > 32) {
                        throw new NumberFormatException();
                    }
                    userService.updateUserState(chatId, State.NO);
                    return List.of(new MessagePayload(chatId, formatResult(propertyService.getPropertiesOnAuction(server)), null, true, false));
                default:
                    return List.of();
            }
        } catch (NumberFormatException e) {
            return List.of(new MessagePayload(chatId, "Номер сервера не может быть меньше нуля иил больше 32", null, false, false));
        }
    }

    @Override
    public String getCommandName() {
        return "getAuctionInfo";
    }

}
