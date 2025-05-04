package org.example.handlers;

import lombok.RequiredArgsConstructor;
import org.example.model.Property;
import org.example.model.State;
import org.example.model.Type;
import org.example.model.User;
import org.example.service.PropertyService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FindByOwnerCommandHandler implements CommandHandler{

    private final PropertyService propertyService;
    private final UserService userService;



    @Override
    public List<BotApiMethod<?>> handle(Update update, User user) {
        Long chatId = getChatId(update);

        switch (user.getState()) {
            case NO:
                userService.updateUserState(chatId, State.WAITING_NICKNAME);
                return List.of(getMessageCommand(chatId, "Введите ник и сервер. Пример Nick_Name 5 или Nick_Name 0(для получения информации со всех серверов)"));
            case WAITING_NICKNAME:
                String text = update.getMessage().getText();
                String[] tmp = text.split(" ");
                if (tmp.length != 2) {
                    return List.of(getMessageCommand(chatId, "Некорректный ввод. Для выхода /quit"));
                }
                try {
                    List<Property> allPropertiesByNickAndServer = propertyService.getAllPropertiesByNickAndServer(tmp[0], Integer.parseInt(tmp[1]));
                    userService.updateUserState(chatId, State.NO);
                    return List.of(getMessageCommand(chatId, formatPropertyInfo(allPropertiesByNickAndServer)));
                }catch (NumberFormatException e) {
                    return List.of(getMessageCommand(chatId, "Некорректный ввод. Для выхода /quit"));
                }
        }
        return List.of();
    }






    @Override
    public String getCommandName() {
        return "findByOwner";
    }
}
