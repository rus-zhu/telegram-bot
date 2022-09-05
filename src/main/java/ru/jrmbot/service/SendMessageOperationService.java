package ru.jrmbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Arrays;
import java.util.List;

import static ru.jrmbot.constant.VarConstant.*;

@Service
public class SendMessageOperationService {
    private final String GREETING_MESSAGE = "Hello! Let's go planning";
    private final String PLANNING_MESSAGE = "Insert deals, after planning press 'End planning'";
    private final String END_PLANNING_MESSAGE = "Planning is ended, to show press 'Show deals'";
    private final String INSTRUCTIONS = "Do you want to read instructions?";
    private final ButtonService buttonService;

    public SendMessageOperationService(ButtonService buttonService) {
        this.buttonService = buttonService;
    }

    public SendMessage createGreetingInformation(Update update) {
        SendMessage message = createSimpleMessage(update, GREETING_MESSAGE);
        ReplyKeyboardMarkup keyboardMarkup =
                buttonService.setButtons(
                        buttonService.createButtons(
                                Arrays.asList(START_PLANNING, END_PLANNING, SHOW_DEALS)));
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    public SendMessage createPlanningMessage(Update update) {
        return createSimpleMessage(update, PLANNING_MESSAGE);
    }

    public SendMessage createEndPlanningMessage(Update update) {
        return createSimpleMessage(update, END_PLANNING_MESSAGE);
    }

    public SendMessage createSimpleMessage(Update update, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText(message);
        return sendMessage;
    }
    public SendMessage createSimpleMessage(Update update, List<String> messages) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        StringBuilder message = new StringBuilder();
        for (String s : messages) {
            message.append(s).append("\n");
        }
        sendMessage.setText(message.toString());
        return sendMessage;
    }

    public SendMessage createInstructionMessage(Update update) {
        SendMessage sendMessage = createSimpleMessage(update, INSTRUCTIONS);
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonService.setInlineKeyMarkup(buttonService.createInlineButton(YES));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public EditMessageText createEditMessage(Update update, String instruction) {
        EditMessageText editMessageText = new EditMessageText();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(instruction);
        return editMessageText;
    }
}
