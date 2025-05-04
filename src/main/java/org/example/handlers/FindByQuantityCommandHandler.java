package org.example.handlers;

import jakarta.persistence.Column;
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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindByQuantityCommandHandler implements CommandHandler {
    private final PropertyService propertyService;
    private final UserService userService;
    private String info = "Введите номер сервера, кол-во домов, промежуток, кол-во бизов, промежуток. \nВсе поля заполняются через пробел, в случае необходимости пропустить параметр вводите 0\nПример: 5 0 0 10 15-25\nГде 5 - номер сервера, 0 пропуск домов, 0 пропуск промежутка домов, 10 кол-во бизов, 15-25 промежуток бизов";


    @Override
    public List<BotApiMethod<?>> handle(Update update, User user) {
        Long chatId = getChatId(update);
        switch (user.getState()) {
            case NO:
                userService.updateUserState(chatId, State.WAITING_HOUSEQ);
                return List.of(getMessageCommand(chatId, info));
            case WAITING_HOUSEQ:
                try {
                    String text = update.getMessage().getText();
                    String[] tmp = text.split(" ");
                    if (tmp.length != 5) {
                        return List.of(getMessageCommand(chatId, "Некорректный ввод. Для выхода введите /quit"));
                    }
                    List<Property> properties = propertyHandler(tmp);
                    if (properties.isEmpty()) {
                        userService.updateUserState(chatId, State.NO);
                        return List.of(getMessageCommand(chatId, "Игроков не найдено"));
                    }
                    userService.updateUserState(chatId, State.NO);
                    return List.of(getMessageCommand(chatId, formatPropertyInfo(properties)));
                } catch (Exception e) {
                    return List.of(getMessageCommand(chatId, "Некорректный ввод. Для выхода введите /quit"));
                }

        }
        return List.of();

    }


    private List<Property> propertyHandler(String[] tmp) {
        try {
            Integer server = Integer.parseInt(tmp[0]);
            if ("0".equals(tmp[1]) && !"0".equals(tmp[3])) {
                int quantity = Integer.parseInt(tmp[3]);
                if ("0".equals(tmp[4])) {
                    return propertyService.getPropertiesByQuantityAndType(server, Type.Business, quantity);
                } else {
                    List<Integer> promList = getProm(tmp[4]);
                    return propertyService.getPropertiesListByTypeServerPromAndCount(server, Type.Business, promList.get(0), promList.get(1), quantity);
                }
            } else if ("0".equals(tmp[3]) && !"0".equals(tmp[1])) {
                int quantity = Integer.parseInt(tmp[1]);
                if (!"0".equals(tmp[2])) {
                    return propertyService.getPropertiesByQuantityAndType(server, Type.House, quantity);
                } else {
                    List<Integer> promList = getProm(tmp[4]);
                    return propertyService.getPropertiesListByTypeServerPromAndCount(server, Type.House, promList.get(0), promList.get(1), quantity);
                }
            }
            return List.of();

        } catch (NumberFormatException e) {
            throw new RuntimeException();

        }


    }

    private List<Integer> getProm(String str) {
        try {
            List<Integer> result = new ArrayList<>();
            String[] prom = str.split("-");
            int prom1 = Integer.parseInt(prom[0]);
            int prom2 = Integer.parseInt(prom[1]);
            result.add(prom1);
            result.add(prom2);
            return result;
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public String getCommandName() {
        return "findByQuantity";
    }
}
