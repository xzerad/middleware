import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Auth extends JFrame {

    ////Part 1
    MongoClient Client ;
    MongoDatabase MiddleWare ;
    MongoCollection<Document> users;
    Document insert1,insert2,insert3;
    List<Document> list ;



    Auth() {

        //part 1
        Client = MongoClients.create("mongodb://localhost:27017");
        MiddleWare = Client.getDatabase("MiddleWare");
        users = MiddleWare.getCollection("users");

        insert1=new Document();
        insert1.append("email","soniabahri");
        insert1.append("password","soniabahri");
        insert2=new Document("name","alahmida").append("password","alahmida");
        insert3=new Document();
        insert3.append("email","radwanchaieb");
        insert3.append("password","radwanchaieb");

        list= new ArrayList<Document>();
        list.add(insert1);list.add(insert2);list.add(insert3);

        MiddleWare.getCollection("users").insertMany(list);
        System.out.println("Documents inserted succesfully ! ");



    }

    public static void main(String[] args) {
        new Auth();
    }


    static class Exist {

        public Exist(String text, String text1) {

        }
    } }
