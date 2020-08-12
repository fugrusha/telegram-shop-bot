package org.golovko.telegramshop.config;

import org.golovko.telegramshop.MyTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class BotConfig {

    @Value("${telegram.bot.webHook.path}")
    private String webHookPath;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Bean
    public MyTelegramBot myTelegramBot() {
        MyTelegramBot bot = new MyTelegramBot();
        bot.setWebHookPath(webHookPath);
        bot.setBotUserName(botUsername);
        bot.setBotToken(botToken);

        return bot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
