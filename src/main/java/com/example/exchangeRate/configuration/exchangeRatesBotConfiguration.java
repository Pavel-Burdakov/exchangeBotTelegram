package com.example.exchangeRate.configuration;
import com.example.exchangeRate.bot.exchangeRatesBot;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class exchangeRatesBotConfiguration {

    // этим методом мы сообщаем библиотеке, что создали класс для обработки телеграмм запросов
    // в качестве аргумента прилетает экземпляр нашего класса и нам нужно зарегистрировать его в TelegramBotsApi
    // возвращаем экземпляр класса с зарегистрированным ботом
    @Bean
    public TelegramBotsApi telegramBotsApi(exchangeRatesBot exchangeRatesBot) throws TelegramApiException {
        var api  =  new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(exchangeRatesBot);
        return api;
    }

    @Bean
    public OkHttpClient okHttpClient(){
        return new OkHttpClient();
    }
}
