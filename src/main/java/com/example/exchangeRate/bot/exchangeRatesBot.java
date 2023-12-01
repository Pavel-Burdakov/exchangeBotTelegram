package com.example.exchangeRate.bot;
import com.example.exchangeRate.exception.serviceException;
import com.example.exchangeRate.services.ExchangeRateService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.logging.Logger;

@Component
public class exchangeRatesBot extends TelegramLongPollingBot {
    @Autowired
    private ExchangeRateService exchangeRateService;
    // в лог будем писать события на всякий случай
    //private static final Logger LOG = (Logger) LoggerFactory.getLogger(exchangeRatesBot.class);
    // команды для бота
    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String HELP = "/help";

    public exchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    // здесь обрабатываются пользовательские команды
    @Override
    public void onUpdateReceived(Update update) {
        // для проверки есть ли вообще сообщение от пользователя
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        //  если сообщение пришло вытаскиваем его в отдельную переменную
        var message = update.getMessage().getText();
        // идентификатор чата, чтобы ответить тому пользователю от которого получен запрос
        var chatId = update.getMessage().getChatId();
        //  проверяем значение из сообщения на соответствие одной из команд и обрабатываем его
        //  пока обработка в методах здесь же, но лучше вынести для каждого обработку в отдельный класс
        switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }

    // возвращает имя бота
    @Override
    public String getBotUsername() {
        return "pavel_b_exchangeBot";
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                
                Здесь Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                
                Для этого воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                
                Дополнительные команды:
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }



    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //LOG.log("Ошибка отправки сообщения", e);
        }
    }

    private void usdCommand(Long chatId) {
        String formattedText = null;
        try {
            var usd = exchangeRateService.getUSDCurrencyRate();
            var text = "Курс доллара на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (serviceException e) {
           /* LOG.error("Ошибка получения курса доллара", e);
            formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже.";*/
        }
        sendMessage(chatId, formattedText);
    }

    private void eurCommand(Long chatId) {
        String formattedText = null;
        try {
            var usd = exchangeRateService.getEURCurrencyRate();
            var text = "Курс евро на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (serviceException e) {
           /* LOG.error("Ошибка получения курса евро", e);
            formattedText = "Не удалось получить текущий курс евро. Попробуйте позже.";*/
        }
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту
                
                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
    }

}
