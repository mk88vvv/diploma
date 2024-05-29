import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Handler extends TelegramLongPollingBot {
    Database database;
    public Handler(Database database) {
        this.database=database;
    }

    @Override
    public String getBotUsername() {
        return "@innoprog_analysis_bot";
    }

    @Override
    public String getBotToken() {
        return "6172903723:AAH2KBS34asgBDxz4KMV9aBCDtIul9rIIn8";
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            if (message.isCommand()){
                if (message.getText().equals("/start")){
                    execute(Commands.handleStart(update));
                }
               else if (message.getText().startsWith("/get_client")){
                    String[] splittedmessage;
                    splittedmessage=message.getText().split(" ");
                    execute(Commands.showClientInfo(update,database, splittedmessage[1]));
                }
               else if(message.getText().startsWith("/get_performed_modules")){
                   String[] splittedmessage;
                   splittedmessage=message.getText().split(" ");
                   execute(Commands.getPerformedModules(update,database,splittedmessage[1]));
                }
                else if(message.getText().startsWith("/get_total_price")){
                    String[] splittedmessage;
                    splittedmessage=message.getText().split(" ");
                    execute(Commands.getTotalPrice(update,database,Long.parseLong(splittedmessage[1])));
                }
                else if(message.getText().startsWith("/get_further_profit")){
                    execute(Commands.getFurtherProfit(update,database));
                }
            }
        }
    }


}
