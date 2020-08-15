package org.golovko.telegramshop.botapi;

import org.golovko.telegramshop.botapi.handler.InputMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {

    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    @Autowired
    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public BotApiMethod<?> processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentBotState) {
        if (isFillingOrderInfoState(currentBotState)) {
            return messageHandlers.get(BotState.FILLING_ORDER_INFO);
        }

        return messageHandlers.get(currentBotState);
    }

    private boolean isFillingOrderInfoState(BotState botState) {
        switch (botState) {
            case ENTER_FULL_NAME:
            case ENTER_PHONE:
            case ENTER_CITY:
            case ENTER_ADDRESS:
            case CONFIRM_ORDER:
                return true;
            default:
                return false;
        }
    }
}
