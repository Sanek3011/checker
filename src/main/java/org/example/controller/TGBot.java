package org.example.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.handlers.CommandHandler;
import org.example.model.State;
import org.example.model.User;
import org.example.service.UserService;
import org.example.util.KeyboardUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j

public class TGBot extends TelegramLongPollingBot {

    @Value(value = "${telegram.bot.username}")
    private String BOT_NAME;
    @Value(value = "${telegram.bot.token}")
    private String BOT_TOKEN;
    private final UserService userService;
    private final Map<String, CommandHandler> commandHandlers;

    public TGBot(UserService userService, List<CommandHandler> handlers) {
        this.userService = userService;
        this.commandHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        CommandHandler::getCommandName,
                        Function.identity()

                ));
    }

    @PostConstruct
    public void init(){
        registerCommands();
    }

    @Override
    public void onUpdateReceived(Update update) {
        User user;

        if (update.hasMessage() && update.getMessage().hasText()) {
            textHandler(update);

        }
        if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            user = userService.getUserByTgId(chatId);
            callbackHandler(update, user);
        }
    }

    private void callbackHandler(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        System.out.println(data);
        switch (data) {
            default:
                CommandHandler commandHandler = commandHandlers.get(data);
                if (commandHandler != null) {
                    execute(commandHandler.handle(update, user));
                }
        }
    }

    private void textHandler(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        User user = userService.getUserByTgId(chatId);
        if ("/start".equals(text) && user == null) {
            userService.saveUser(chatId);
            sendMessageToUser(chatId, "Успешно зарегистрирован");
        }

        if ("/quit".equals(text) || "quit".equals(text) || "/keyboard".equals(text)) {
            userService.updateUserState(chatId, State.NO);
            sendKeyboard(chatId, KeyboardUtil.getMenuKeyboard());
            return;
        }
        handleState(update, user);
    }

    private void handleState(Update update, User user) {
        List<BotApiMethod<?>> handle;
        switch (user.getState()) {
            case WAITING_NICKNAME:
                handle = commandHandlers.get("findByOwner").handle(update, user);
                execute(handle);
                break;
            case WAITING_HOUSEQ:
                handle = commandHandlers.get("findByQuantity").handle(update, user);
                execute(handle);
        }

    }


    public void sendKeyboard(Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Нажмите на одну из кнопок");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.warn("Ошибка при отправке клавиатуры");
        }
    }

    public void sendMessageToUser(Long chatId, String text) {
        log.info("Отправка сообщения к {}", chatId);
        int maxLength = 4000;

        for (int i = 0; i < text.length(); i += maxLength) {
            String part = text.substring(i, Math.min(i + maxLength, text.length()));
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(part);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.warn("Отправка сообщения не удалась к {}", chatId);
            }
        }
    }
    private void sendAsFile(Long chatId, String text) {
        try {
            File tmp = File.createTempFile("msg_part", ".txt");
            try (FileWriter fw = new FileWriter(tmp, StandardCharsets.UTF_8)) {
                fw.write(text);
            }
            SendDocument doc = new SendDocument();
            doc.setChatId(chatId);
            doc.setDocument(new InputFile(tmp, "message.txt"));
            execute(doc);
            tmp.delete();
            // можно тут tmp.deleteOnExit();
        } catch (IOException | TelegramApiException ex) {
            log.error("Не удалось отправить файл с сообщением", ex);
        }
    }

    public void execute(List<BotApiMethod<?>> commands) {
        for (BotApiMethod<?> action : commands) {
            try {
                execute(action);
            } catch (TelegramApiException e) {
                log.warn("Не удалось выполнить {}", action);
                if (e.getMessage() != null && e.getMessage().contains("message is too long")) {
                    if (action instanceof SendMessage) {
                        SendMessage msg = (SendMessage) action;
                        sendAsFile(Long.valueOf(msg.getChatId()), msg.getText());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void registerCommands() {
        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/keyboard", "Показать клавиатуру"));
        commands.add(new BotCommand("/quit", "Выйти в меню(обнулить действие)"));


        try {
            this.execute(new SetMyCommands(commands, null, null));
        }catch (TelegramApiException e) {
            e.printStackTrace();
            log.warn("Команды не зарегистрированы. registerCommands()");
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }
}
