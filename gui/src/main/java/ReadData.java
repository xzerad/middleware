import com.mongodb.client.*;
import org.bson.Document;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ReadData {



    MongoCollection<Document> users;

    ReadData() {
        MongoClient Client ;
        MongoDatabase MiddleWare ;
        Client = MongoClients.create("mongodb://localhost:27017");
        MiddleWare = Client.getDatabase("MiddleWare");
        users = MiddleWare.getCollection("users");

    }

    boolean verify(String email, String password) {

        Document user = users.find(and(eq("email", email), eq("password", password))).first();
        return user != null ;
        }


    public static void main(String[] args) {
        ReadData readdata = new ReadData();
        boolean test = readdata.verify("soniabahri","soniabahri");
        System.out.println(test);
    }
}