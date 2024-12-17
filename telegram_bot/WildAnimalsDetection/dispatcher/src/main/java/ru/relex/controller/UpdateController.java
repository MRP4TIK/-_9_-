/*
package ru.relex.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public  void processUpdate(Update update){
        if(update == null){
            log.error("Received update is null");
            return;
        }

        if (update.getMessage() != null){
            distributeMessagesByType(update);
        } else {
            log.error("Received unsupported messages type " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if(message.getText()!=null){
            prcessTextMessage(update);
        }else if(message.get)
    }

    private void prcessTextMessage(Update update) {
    }
}
*/
