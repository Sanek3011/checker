package org.example;

import org.example.controller.TGBot;
import org.example.model.Type;
import org.example.model.util.MessagePayload;
import org.example.service.PropertyService;
import org.example.util.KeyboardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class App 
{
    public static void main( String[] args )
    {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(App.class)
                .web(WebApplicationType.NONE)
                .run(args);
        try {
            TGBot bean = context.getBean(TGBot.class);
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bean);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void test(PropertyService service) {

    }
}
