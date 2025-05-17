package org.example.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Property;
import org.example.model.State;
import org.example.model.Type;
import org.example.model.User;
import org.example.model.util.MessagePayload;
import org.example.service.PropertyService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindByInterval implements CommandHandler {
    private final PropertyService propertyService;
    private final UserService userService;

    @Override
    public List<MessagePayload> handle(Update update, User user) {
        Long chatId = getChatId(update);
        try {
            switch (user.getState()) {
                case NO:
                    userService.updateUserState(chatId, State.WAITING_INTERVAL);
                    return List.of(new MessagePayload(chatId, "Введите сервер, промежуток и тип. Доступные типы: 0 - дом, 1 - бизнес. Пример: 2 10-20 1, где 2 - номер сервера, 10-20 промежуток, а 1 - бизнес", null, false, false));
                case WAITING_INTERVAL:
                    String text = update.getMessage().getText();
                    String[] tmp = text.split(" ");
                    Integer server = Integer.parseInt(tmp[0]);
                    String[] interval = tmp[1].split("-");
                    Type type = getTypeByString(tmp[2]);
                    if (tmp.length != 3
                        || interval.length != 2) {
                      throw new RuntimeException();
                    }
                    userService.updateUserState(chatId, State.NO);
                    List<Property> resultList = propertyService.findPropertyByServerAndIntervalAndType(server, type, Integer.parseInt(interval[0]), Integer.parseInt(interval[1]));
                    return List.of(new MessagePayload(chatId, formatResult(resultList), null, true, false));
                default:
                    return List.of();

            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            e.printStackTrace();
            return List.of(new MessagePayload(chatId, "Некорректный ввод. Для выхода введите /quit", null, false, false));
        }
    }

    @Override
    public String getCommandName() {
        return "findByInterval";
    }

    private Type getTypeByString(String type) {
        if ("0".equals(type)) {
            return Type.House;
        } else if ("1".equals(type)){
            return Type.Business;
        }else{
            throw new RuntimeException();
        }
    }

}
