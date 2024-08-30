package umg.progra2.botTelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class botCuestionario extends TelegramLongPollingBot {
    private final Map<Long, Integer> indicePregunta = new HashMap<>();
    private final Map<Long, String> seccionActiva = new HashMap<>();
    private final Map<String, String[]> preguntas = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "OsvalrBot";
    }

    @Override
    public String getBotToken() {
        return "7305646869:AAElQxTmYlu1utjmaIptbJIhz1p_etMA4bQ";
    }

    public botCuestionario() {
        // Inicializa los cuestionarios con las preguntas.
        preguntas.put("SECTION_1", new String[]{"🤦‍♂1.1- Estas aburrido?", "😂😂 1.2- Te bañaste hoy?", "🤡🤡 Pregunta 1.3"});
        preguntas.put("SECTION_2", new String[]{"Pregunta 2.1", "Pregunta 2.2", "Pregunta 2.3"});
        preguntas.put("SECTION_3", new String[]{"Pregunta 3.1", "Pregunta 3.2", "Pregunta 3.3"});
        preguntas.put("SECTION_4", new String[]{"Pregunta 4.1", "¿Cuál es tu edad?", "Pregunta 4.3"}); // Sección 4 con validación de edad
    }

    @Override
    public void onUpdateReceived(Update actualizacion) {
        if (actualizacion.hasMessage() && actualizacion.getMessage().hasText()) {
            String messageText = actualizacion.getMessage().getText();
            long chatId = actualizacion.getMessage().getChatId();

            // Verifica si el usuario está registrado
            if (isUserRegistered(chatId)) {
                if (messageText.equals("/menu")) {
                    sendMenu(chatId);
                } else if (seccionActiva.containsKey(chatId)) {
                    manejaCuestionario(chatId, messageText);
                }
            } else {
                // Si el usuario no está registrado, pedir correo y registrar
                sendText(chatId, "Por favor, regístrate enviando tu correo electrónico.");
                registerUser(chatId, messageText); // Implementar el método de registro
            }
        } else if (actualizacion.hasCallbackQuery()) { // es una respuesta de un botón
            String callbackData = actualizacion.getCallbackQuery().getData();
            long chatId = actualizacion.getCallbackQuery().getMessage().getChatId();
            inicioCuestionario(chatId, callbackData);
        }
    }

    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Selecciona una sección:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Crea los botones del menú
        rows.add(crearFilaBoton("Sección 1", "SECTION_1"));
        rows.add(crearFilaBoton("Sección 2", "SECTION_2"));
        rows.add(crearFilaBoton("Sección 3", "SECTION_3"));
        rows.add(crearFilaBoton("Sección 4", "SECTION_4")); // seccion agregada

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> crearFilaBoton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }

    private void inicioCuestionario(long chatId, String section) {
        seccionActiva.put(chatId, section);
        indicePregunta.put(chatId, 0);
        enviarPregunta(chatId);
    }

    private void enviarPregunta(long chatId) {
        String seccion = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);
        String[] questions = preguntas.get(seccion);

        if (index < questions.length) {
         sendText(chatId, "Hola " + (chatId) + ", bienvenido al sistema de registro de la UMG. ¿En qué puedo ayudarte?");
            if (seccion.equals("SECTION_4") && index == 1) {
                sendText(chatId, questions[index]);
            } else {
                sendText(chatId, questions[index]);
            }
        } else {
            sendText(chatId, "¡Has completado el cuestionario!");
            seccionActiva.remove(chatId);
            indicePregunta.remove(chatId);
        }
    }

    private void manejaCuestionario(long chatId, String response) {
        String section = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);

        if (section.equals("SECTION_4") && index == 1) {
            // Validación de edad
            try {
                int edad = Integer.parseInt(response);
                if (edad < 0 || edad > 120) {
                    sendText(chatId, "Edad inválida. Por favor, ingresa una edad válida.");
                    return;
                }
            } catch (NumberFormatException e) {
                sendText(chatId, "Por favor, ingresa un número para la edad.");
                return;
            }
        }

        // Guardar respuesta en la base de datos
        saveResponse(chatId, section, index, response);

        sendText(chatId, "Tu respuesta fue: " + response);
        indicePregunta.put(chatId, index + 1);

        enviarPregunta(chatId);
    }

    private void saveResponse(long chatId, String section, int questionId, String response) {
        // Implementa el código para guardar la respuesta en la base de datos
        // Utiliza un servicio para guardar la respuesta en la tabla tb_respuestas
    }

    private boolean isUserRegistered(long chatId) {
        // Implementa la lógica para verificar si el usuario está registrado en la base de datos
        return false;
    }

    private void registerUser(long chatId, String email) {
        // Implementa la lógica para registrar al usuario en la base de datos usando el correo electrónico
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
