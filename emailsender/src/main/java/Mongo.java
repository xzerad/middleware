

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

import javax.swing.text.Document;
import java.time.LocalDateTime;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class Mongo {
    MongoCollection collection;


    Mongo(){
        String uri="mongodb://localhost:27017";
        MongoClientURI clientURI = new MongoClientURI(uri);
        MongoClient mongoClient = new MongoClient(clientURI);

        MongoDatabase mongoDatabase = mongoClient.getDatabase("MiddleWare");
        collection= mongoDatabase.getCollection("email_data");
    }


    public void update(String email,String l ,String response_date){

        collection.updateOne(eq("email", email), combine(set("response_date", response_date), set("state",l )));



    }

}
