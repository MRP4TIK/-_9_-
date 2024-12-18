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

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                // Формируем приветственное сообщение
                String welcomeMessage = "\uD83D\uDC4B Привет! Добро пожаловать в нашего бота по распознаванию диких животных!\n" +
                        "\n" +
                        "\uD83D\uDC3B Этот бот предназначен для помощи в распознавании диких животных, которые могут выйти к людям в различных регионах России. На данный момент мы можем определить только медведя и тигра.\n" +
                        "\n" +
                        "\uD83D\uDCF8 Чтобы начать, просто отправьте фотографию животного, и мы постараемся помочь вам с его идентификацией!";

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

             if(result.equals("0")){
                 sendTextMessage(message.getChatId(), " ❌ На фото зверь не обнаружен!");
             } else if(result.equals("1")){
                 sendTextMessage(message.getChatId(), "\uD83D\uDC3B На фото обнаружен дикий зверь!");
             } else {
                 // Возвращаем результат пользователю
                 sendTextMessage(message.getChatId(), "Произошла ошибка при обработке фотографии.");
             }

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
            ProcessBuilder pb = new ProcessBuilder("python","analyze_photo.py", photo.getAbsolutePath());
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
