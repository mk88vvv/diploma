import org.checkerframework.checker.units.qual.A;

import javax.ws.rs.client.Client;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;

public class Database {
    Connection connection;

    public Database(String url) {
        // Database connection parameters
        String user = "postgres";
        String password = "5852";

        // Establish a connection to the database
        try  {
            Connection conn = DriverManager.getConnection(url, user, password);
            connection = conn;
            System.out.println("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to PostgreSQL database");
            e.printStackTrace();
        }
    }
    public ArrayList<Clientdb> getClientInfo(String username){
        ArrayList<Clientdb> clients = new ArrayList<>();
        String sql = "SELECT id,username FROM public.client WHERE username LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%"+username+"%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String usernamedb = rs.getString("username");
                Clientdb client = new Clientdb(id,usernamedb);
                clients.add(client);
                if(clients.size()==10){
                    return clients;
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute SELECT query");
            e.printStackTrace();
        }
        return clients;
    }
    private HashMap<String,ArrayList<Integer>> getTopicsRange(){
        String sql1 = "SELECT name FROM public.module";
        HashMap<String,ArrayList<Integer>> modules= new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql1)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String name = rs.getString("name");
                ArrayList<Integer> topics = new ArrayList<>();
                modules.put(name,topics);
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to execute SELECT query");
            e.printStackTrace();
        }
        for(String name:modules.keySet()){
            String sql2 = "SELECT id FROM public.topic WHERE module=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql2)){
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    Integer topic  = rs.getInt("id");
                    modules.get(name).add(topic);
                }
            }
            catch (SQLException e) {
                System.out.println("Failed to execute SELECT query");
                e.printStackTrace();
            }
        }
        return modules;



        }
    public ArrayList<String> getPerformedModules(Long id){
        ArrayList<Integer> PerformedTasks = new ArrayList<>();
        ArrayList<String> PerformedModules = new ArrayList<>();
        String sql1 = "SELECT topic_id FROM client_progress WHERE client_id = ? AND is_done = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql1)){
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Integer topic = rs.getInt("topic_id");
                PerformedTasks.add(topic);
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to execute SELECT query");
            e.printStackTrace();
        }
        HashMap<String,ArrayList<Integer>> modules = getTopicsRange();
        for(String name:modules.keySet()){
            boolean flag=true;
            for (Integer topic:modules.get(name)){
                if(!PerformedTasks.contains((topic))){
                    flag=false;
                    break;
                }
            } if(flag) PerformedModules.add(name);
        }
        return PerformedModules;
    }
    private double getTotalDiscount(Long clientId){
        String sql = "SELECT discount FROM rank INNER JOIN client ON rank.name = client.rank WHERE client.id = ?";
        String sql1 = "SELECT SUM(discount) as discount FROM achievements INNER JOIN client_achievements ON achievements.id = client_achievements.achievements_id" +
                " WHERE client_achievements.client_id=?";
        Double discount = 0.0;
        Double sumDiscount = 0.0;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setLong(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                discount = rs.getDouble("discount");;
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to execute SELECT query");
            e.printStackTrace();
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql1)){
            stmt.setLong(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                sumDiscount = rs.getDouble("discount");;
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to execute SELECT query");
            e.printStackTrace();
        }
        return discount+sumDiscount;
    }
    public double getTotalPrice(Long clientId){
        double totalDiscount=getTotalDiscount(clientId);
        double totalPrice=0;
        String sql ="SELECT cost FROM tariff WHERE id = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                totalPrice = rs.getDouble("cost");;
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to execute SELECT query");
            e.printStackTrace();
        }
        totalPrice-=totalPrice*totalDiscount/100;
        return totalPrice;
    }
    private ArrayList<Long> getActiveClients(){
        ArrayList<Long> activeClients = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTimeString = currentTime.format(formatter);
        String sql=String.format("SELECT client_id FROM purchases WHERE start_date < '%s' AND finish_date > '%s'", currentTimeString,currentTimeString);
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                activeClients.add(rs.getLong("client_id"));
            }
        }
        catch (SQLException e) {
            System.out.println("Failed to execute SELECT query");
            e.printStackTrace();
        }
        return activeClients;
    }
    public String getFurtherProfitInfo(){
        StringBuilder info = new StringBuilder();
        ArrayList<Long> activeClients = getActiveClients();
        for (int i = 0; i < activeClients.size(); i++) {
            info.append(activeClients.get(i)).append(" - ").append(getTotalPrice(activeClients.get(i))).append("R\n");
        }
        info.append("\n");
        return info.toString();

    }
    public double getFurtherProfit(){
        double furtherProfit=0;
        ArrayList<Long> activeClients = getActiveClients();
        for (int i = 0; i <activeClients.size(); i++) {
            furtherProfit+=getTotalPrice(activeClients.get(i));
        }
        return furtherProfit;
    }
}