package org.example.handlers;

import lombok.RequiredArgsConstructor;
import org.example.model.Property;
import org.example.model.State;
import org.example.model.Type;
import org.example.model.User;
import org.example.model.util.MessagePayload;
import org.example.service.PropertyService;
import org.example.service.UserService;
import org.springframework.stereotype.Component;
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
    public List<MessagePayload> handle(Update update, User user) {
        Long chatId = getChatId(update);
        switch (user.getState()) {
            case NO:
                userService.updateUserState(chatId, State.WAITING_HOUSEQ);
                return List.of(new MessagePayload(chatId, info, null, false, false));
            case WAITING_HOUSEQ:
                try {
                    String text = update.getMessage().getText();
                    String[] tmp = text.split(" ");
                    if (tmp.length != 5) {
                        throw new RuntimeException();
                    }
                    List<Property> properties = propertyHandler(tmp);
                    if (properties.isEmpty()) {
                        userService.updateUserState(chatId, State.NO);
                        return List.of(new MessagePayload(chatId, "Игроков не найдено", null, false, false));
                    }
                    userService.updateUserState(chatId, State.NO);
                    return List.of(new MessagePayload(chatId, formatPropertyInfo(properties), null, true, false));
                } catch (Exception e) {
                    e.printStackTrace();
                    return List.of(new MessagePayload(chatId, "Некорректный ввод. Для выхода введите /quit", null, false, false));

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
                if ("0".equals(tmp[2])) {
                    return propertyService.getPropertiesByQuantityAndType(server, Type.House, quantity);
                } else {
                    List<Integer> promList = getProm(tmp[2]);
                    return propertyService.getPropertiesListByTypeServerPromAndCount(server, Type.House, promList.get(0), promList.get(1), quantity);
                }
            }else{
                int quantityH = Integer.parseInt(tmp[1]);
                List<Integer> promH = getProm(tmp[2]);
                int promH1 = 0;
                int promH2 = 2500;
                if (!promH.isEmpty()){
                    promH1 = promH.get(0);
                    promH2 = promH.get(1);
                }
                int quantityB = Integer.parseInt(tmp[3]);
                List<Integer> promB = getProm(tmp[4]);
                int promB1 = 0;
                int promB2 = 500;
                if (!promB.isEmpty()){
                    promB1 = promB.get(0);
                    promB2 = promB.get(1);
                }
                return propertyService.getAllPropertiesByQuantityAndPromDouble(server, quantityH, promH1, promH2, quantityB, promB1, promB2);
            }


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
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String getCommandName() {
        return "findByQuantity";
    }
}
