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
//        if (isFillingProfileState(currentBotState)) {
//            return messageHandlers.get(BotState.FILLING_PROFILE);
//        }

        return messageHandlers.get(currentBotState);
    }

//    private boolean isFillingProfileState(BotState botState) {
//        switch (botState) {
//            case ASK_EMAIL:
//            case ASK_PHONE:
//            case ASK_DATE:
//            case ASK_TIME:
//            case FINISH_APPLICATION:
//                return true;
//            default:
//                return false;
//        }
//    }
}
