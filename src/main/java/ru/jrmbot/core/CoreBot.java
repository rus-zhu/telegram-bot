package ru.jrmbot.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.jrmbot.service.SendMessageOperationService;
import ru.jrmbot.store.HashMapStore;

import java.time.LocalDate;

import static ru.jrmbot.constant.VarConstant.*;

@Component
public class CoreBot extends TelegramLongPollingBot {

    private final SendMessageOperationService sendMessageOperationService;
    private final HashMapStore store;
    private boolean isStartPlanning;
    @Value("${token}")
    private String token;
    @Value("${bot.username}")
    String botUsername;

    public CoreBot(SendMessageOperationService sendMessageOperationService, HashMapStore store) {
        this.sendMessageOperationService = sendMessageOperationService;
        this.store = store;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            switch (update.getMessage().getText()) {
                case START -> {
                    isStartPlanning = true;
                    executeMessage(sendMessageOperationService.createGreetingInformation(update));
                    executeMessage(sendMessageOperationService.createInstructionMessage(update));
                }
                case START_PLANNING -> executeMessage(sendMessageOperationService.createPlanningMessage(update));
                case END_PLANNING -> {
                    isStartPlanning = false;
                    executeMessage(sendMessageOperationService.createEndPlanningMessage(update));
                }
                case SHOW_DEALS -> {
                    if (!isStartPlanning) {
                        executeMessage(sendMessageOperationService.createSimpleMessage(update, store.selectAll(LocalDate.now())));
                    }
                }
                default -> {
                    if (isStartPlanning) {
                        store.save(LocalDate.now(), update.getMessage().getText());
                    }
                }
            }
        }
        if (update.hasCallbackQuery()) {
            String instruction = "Bot for creating deal on day. For use bot follow the instructions";
            String callData = update.getCallbackQuery().getData();
            switch (callData) {
                case YES -> {
                    EditMessageText text = sendMessageOperationService.createEditMessage(update, instruction);
                    executeMessage(text);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }


    @Override

    public String getBotToken() {
        return token; //telegram bot token
    }

    private <T extends BotApiMethod> void executeMessage(T sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
