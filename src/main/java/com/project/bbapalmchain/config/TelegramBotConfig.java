//package com.project.bbapalmchain.config;
//
//import com.project.bbapalmchain.service.MetadataTelegramBot;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Configuration
//@RequiredArgsConstructor
//public class TelegramBotConfig {
//
//    private final MetadataTelegramBot metadataTelegramBot;
//
//    @Bean
//    public TelegramBotsApi telegramBotsApi() throws Exception {
//        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//        botsApi.registerBot(metadataTelegramBot);
//        return botsApi;
//    }
//}
