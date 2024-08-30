package umg.progra2;

    import org.telegram.telegrambots.bots.TelegramLongPollingBot;
    import org.telegram.telegrambots.meta.api.objects.Update;
    import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
    import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.List; // Importar List long

    public class tareaBot extends TelegramLongPollingBot {
        //usuario de mi bot
        @Override
        public String getBotUsername() {
            return "OsvalrBot";
        }
        //token de mi bot
        @Override
        public String getBotToken() {
            return "7305646869:AAElQxTmYlu1utjmaIptbJIhz1p_etMA4bQ";
        }

        @Override
        public void onUpdateReceived(Update update) {
            // para menejarlos mensajes entrantes
            if (update.hasMessage() && update.getMessage().hasText()) {
                String mensaje = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();


                // Obtener el nombre y apellido del usuario que nos envio mensaje
                String nombUsuario = update.getMessage().getFrom().getFirstName();
                String apellidoUsuario = update.getMessage().getFrom().getLastName();
                Long idUsuario = update.getMessage().getFrom().getId();
              /*  if (apellidoUsuario == null) {
                    apellidoUsuario = ""; //
                } */

                // Me imprime el nombre y apellido en la consola
                System.out.println("NOMBRE USUARIO: " + nombUsuario);
                System.out.println("APELLIDO USUARIO: " + apellidoUsuario);
                System.out.println("El numero de usuario es" + idUsuario);
                SendMessage respuesta = new SendMessage();
                respuesta.setChatId(update.getMessage().getChatId().toString());
                //primer comando  la info
                if (mensaje.equals("/info")) {
                    respuesta.setText("Informacion personal:" +
                            "Numero de carnet: 0905-23\n" +
                            "Nombre: Enner Godoy\n" +
                            "Semestre actual: Cuarto");
                    //segundo comando progra nos da comentarios de lo que nos parece la clase de programacion
                } else if (mensaje.equals("/progra")) {
                    respuesta.setText("Comentarios sobre la clase de programación:\n" +
                            "Esta clase me parece increible porque nos ayuda a ver la tecnologia desde otra perspectiva y nos ayuda a darnos cuenta de lo vulnerable que podemos ser.");
                    //tercer comando  que nos muestra hora y fecha actual
                } else if (mensaje.equals("/hola")) {
                    // Obtiene el nombre del usuario
                    String nombreUsuario = update.getMessage().getFrom().getFirstName();

                    // Obtiene la fecha y hora actual
                    LocalDateTime ahora = LocalDateTime.now();
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");
                    String fechaHora = ahora.format(formato);

                    // Crea el mensaje de respuesta
                    String saludo = String.format("Hola, %s, hoy es %s", nombreUsuario, fechaHora);
                    respuesta.setText(saludo);
                    //cuarto comando nos pide cantidad en euros y nos dice cuanto es en quetzales
                } else if  (mensaje.startsWith("/cambio")) {
                        try {
                            // Extrae la cantidad de Euros del mensaje
                            String[] partes = mensaje.split(" ");
                            if (partes.length == 2) {
                                double euros = Double.parseDouble(partes[1]);

                                // Tipo de cambio 1 Euro = 8.87 Quetzales
                                double tipoCambio = 8.87;
                                double quetzales = euros * tipoCambio;

                                // Crea el mensaje de respuesta
                                String respuestaTexto = String.format("Son %.2f Quetzales.", quetzales);
                                respuesta.setText(respuestaTexto);
                                //Ayuda al usuario por si tiene errores
                            } else {
                                respuesta.setText("Por favor, envía la cantidad en Euros después del comando, por ejemplo: /cambio 100");
                            }
                        } catch (NumberFormatException e) {
                            respuesta.setText("La cantidad proporcionada no es válida. Por favor, introduce un número.");
                        }
                } else if (mensaje.startsWith("/grupal ")) {
                    // Mensaje predefinido para el comando /grupal
                    String mensajeGrupal = "hola amigos";

                    // Lista de chat_id de los compañeros
                    List<Long> listaChatIds = List.of(5747730047L, 2085251453L, 6108736830L, 6082604734L); //krave, pablo, marvin, alan

                    for (Long id : listaChatIds) {
                        SendMessage mensajeAEnviar = new SendMessage();
                        mensajeAEnviar.setChatId(id.toString());
                        mensajeAEnviar.setText(mensajeGrupal);

                        try {
                            execute(mensajeAEnviar); // Envía el mensaje a cada chat_id
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    respuesta.setText("Mensaje enviado a todos los compañeros.");

                    } else{
                        respuesta.setText(" Holaaaa, Ingresa un comando ");
                    }

                    try {
                        execute(respuesta);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }





