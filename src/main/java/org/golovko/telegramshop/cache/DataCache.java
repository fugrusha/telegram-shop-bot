package org.golovko.telegramshop.cache;

import org.golovko.telegramshop.botapi.BotState;

public interface DataCache {

    BotState getCurrentBotState(long chatId);

    void setNewBotState(long chatId, BotState botState);
}
