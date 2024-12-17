package ru.relex.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    // Укажите токен и имя бота (можно вынести в application.properties)
    private final String BOT_TOKEN = "7826699732:AAG6hyICBTcqmfLW2MBFQM7Cw47U2F6iOAE";
    private final String BOT_USERNAME = "wild_animal_detection_bot";

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                // Формируем приветственное сообщение
                String welcomeMessage = "\uD83C\uDF1F Приветствую тебя, любитель дикой природы! \uD83D\uDC3E" +
                        " Этот бот создан для захватывающего путешествия в мир диких животных." +
                        " Отправь мне фотографию, и я с радостью помогу определить, есть ли на ней величественный тигр" +
                "\uD83D\uDC05 или могучий медведь \uD83D\uDC3B. Давай исследовать вместе! \uD83C\uDF3F✨";

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(welcomeMessage);

                try {
                    execute(message); // Отправка сообщения
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                sendTextMessage(chatId, "Отправьте фото для анализа.");
            }
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            processPhoto(update.getMessage());
        }
    }

    private void processPhoto(Message message) {
        try {
            String fileId = message.getPhoto().get(0).getFileId();
            GetFile getFile = new GetFile();
            getFile.setFileId(fileId);

            String filePath = execute(getFile).getFilePath();

            // Загружаем фото
            File photoFile = downloadPhoto(filePath);

            // Передаём файл в Python скрипт
             String result = callPythonScript(photoFile);

             
            // Возвращаем результат пользователю
            sendTextMessage(message.getChatId(), "Результат анализа: " + result);

            // Удаляем временный файл
            photoFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            sendTextMessage(message.getChatId(), "Произошла ошибка при обработке фотографии.");
        }
    }

    private File downloadPhoto(String filePath) throws TelegramApiException, IOException {
        byte[] photoBytes = downloadFileAsStream(filePath).readAllBytes();
        File photoFile = File.createTempFile("photo", ".jpg");
        Files.write(photoFile.toPath(), photoBytes);
        return photoFile;
    }

    private String callPythonScript(File photo) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python","C:\\Users\\kelar\\Desktop\\-_9_--main\\analyze_photo.py", photo.getAbsolutePath());
            Process process = pb.start();

            // Читаем результат
            String result = new String(process.getInputStream().readAllBytes());
            process.waitFor();
            return result.trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка анализа.";
        }
    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
