package uz.pdp.online;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Main extends TelegramLongPollingBot {
    static String firstLang = "";
    static String secondLang = "";

    public static void main(String[] args) {

        TelegramBotsApi api = null;
        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        try {
            api.registerBot(new Main());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String getBotUsername() {
        return "Abbosxon_Dictionary_bot";
    }

    @Override
    public String getBotToken() {
        return "5210587515:AAHeGFFECdan8wbL9UfnIixLuntip-ihRvE";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        String input = update.getMessage().getText();

        if (input.toLowerCase().contains("start")) {
            firstLang = "";
            secondLang = "";
            sendMessage.setText("Choose first language:");
            startingAction(sendMessage);
        } else if (firstLang.equals("") && (input.contains("ru") || input.contains("en") || input.contains("tr"))) {
            if (input.contains("en")) {
                firstLang = "en";
            } else if (input.contains("ru")) {
                firstLang = "ru";
            } else if (input.contains("tr")) {
                firstLang = "tr";
            }

            sendMessage.setText("Choose second language:");
            startingAction(sendMessage);
        } else if (secondLang.equals("")&&(input.contains("ru") || input.contains("en") || input.contains("tr"))) {
            if (input.contains("en")) {
                secondLang = "en";
            } else if (input.contains("ru")) {
                secondLang = "ru";
            } else if (input.contains("tr")) {
                secondLang = "tr";
            }
            sendMessage.setText("Input any texts for translating:");
        } else {
            //sendMessage.setText(input);
            try {
                sendMessage.setText(translatingMethod(input, firstLang, secondLang));  // for translating
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            execute(sendMessage);
        } catch (
                TelegramApiException e) {
            e.printStackTrace();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static void startingAction(SendMessage sendMessage) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(markup);
        markup.setResizeKeyboard(true);
        markup.setOneTimeKeyboard(true);
        markup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton("Start(again)");
        row1.add(button1);
        keyboardRows.add(row1);


        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton("English(en)");
        KeyboardButton button3 = new KeyboardButton("Russian(ru)");
        KeyboardButton button4 = new KeyboardButton("Turkish(tr)");
        row2.add(button2);
        row2.add(button3);
        row2.add(button4);

        keyboardRows.add(row2);
        markup.setKeyboard(keyboardRows);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String translatingMethod(String text, String fromLang, String toLang) throws Exception {

        String hurl = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?";
        String api = "dict.1.1.20220217T052917Z.35ce04425a536712.d15ab9baef227f311b155458f4734e88f16e69b4";
        String lang = fromLang + "-" + toLang;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        URL url = new URL(hurl + "&key=" + api + "&text=" + text + "&lang=" + lang);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));


        String output = "";
        String result = "";
        while ((result = reader.readLine()) != null) {
            output += result;
        }

        String lastResult = "";
        //System.out.println(lastResult);
        int textIndex = output.indexOf("text", output.indexOf("text", 0) + 2);
        for (int i = textIndex + 7; i < output.length(); i++) {
            if (output.charAt(i) != ('"')) {
                lastResult += String.valueOf(output.charAt(i));

            } else {
                break;
            }
        }

//       //System.out.println(lastResult);

        return lastResult;

    }
}
