import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;

public class Commands {
    @SneakyThrows
    public static SendMessage handleStart(Update update){
        if(Admins.checkIsAdmin(update.getMessage().getChatId())){
            return SendMessage.builder()//cr interface, commands implements interface
                        .chatId(update.getMessage().getChatId().toString())//!!
                        .text("Добро пожаловать, " + update.getMessage().getFrom().getFirstName())
                        .build();
        }
        else {
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId().toString())
                    .text("Извините, доступ запрещен " + update.getMessage().getFrom().getFirstName())
                    .build();
        }
    }
    public static SendMessage showClientInfo(Update update,Database database, String usernamedb){
        String text_info="";
        ArrayList<Clientdb> clients = database.getClientInfo(usernamedb);
        for (Clientdb client:clients) {
            text_info += "id: " + client.getId() + "\nusername: "+ client.getUsername()+"\n\n";
        }
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(text_info)
                .build();
    }
    public static SendMessage getPerformedModules(Update update,Database database,String id){
        String text_performedmodules="";
        ArrayList<String> performedModules=database.getPerformedModules(Long.parseLong(id));
        for (int i=0;i<performedModules.size();i++){
            text_performedmodules += performedModules.get(i).toString()+"\n";
        }
        if(text_performedmodules.length()==0) text_performedmodules+="Ни одного модуля не выполнено.";
        return  SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(text_performedmodules)
                .build();
    }
    public static SendMessage getTotalPrice(Update update, Database database, Long  ClientId){
        String text_totaldiscount="";
      text_totaldiscount += "Цена с учетом скидки: "+database.getTotalPrice(ClientId)+"\n";
        return  SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(text_totaldiscount)
                .build();
    }
    public static SendMessage getFurtherProfit(Update update, Database database){
        String text_furtherprofit="";
        text_furtherprofit += "Выручка на следующий месяц: \n"+database.getFurtherProfitInfo()+"Общая выручка: "+database.getFurtherProfit()+"\n";
        return  SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text(text_furtherprofit)
                .build();
    }




}
