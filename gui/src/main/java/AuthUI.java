import com.mongodb.client.*;
import org.bson.Document;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AuthUI extends JFrame {

    MongoClient Client;
    ////Part 2
    private final JLabel lb1, lb2;
    JTextField email;
    JPasswordField password;
    JButton btn;
    private Document doc;
    private ArrayList<Document> data;

    AuthUI() {

        ReadData readdata = new ReadData();


        //Part 2
        this.setVisible(true);
        //this.setLayout(FlowLayout);
        lb1 = new JLabel(" Email ");
        email = new JTextField(20);
        lb2 = new JLabel(" Password ");
        password = new JPasswordField(10);
        btn = new JButton("Entrer ");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if(readdata.verify(email.getText(),new String(password.getPassword()))) {
                    new Inter();
                    AuthUI.this.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), " You Can't ! ");
                }
            }
        });

        this.setLayout(new FlowLayout());
        this.add(lb1);
        this.add(email);
        this.add(lb2);
        this.add(password);
        this.add(btn);
        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);


    }

    public static void main(String[] args) {
        new AuthUI();

    }

    Boolean Exist(String email, String pwd) {





//        try {
//            Client = MongoClients.create("mongodb://localhost:27017");
//            MongoDatabase base = Client.getDatabase("middleware");
//            System.out.println("Mongo DB Connected");
//
//            MongoCollection<Document> collection = base.getCollection("users");

//          Cursor cursor = (Cursor) ((MongoCollection<?>) users).find();
//            while(cursor.) {
//            System.out.println(cursor.next());
//            }

//            Document yoyo = users.find(new Document(“email”,"soniabahri”)).first();
//            System.out.println(yoyo.toJson());

//            try (MongoCursor<org.bson.Document> cur = collection.find().iterator()) {
//
//                while (cur.hasNext()) {
//
//                    doc = cur.next();
//                    data = new ArrayList<Document>(doc.values());
//
//                    System.out.printf("%s: %s%n", data.get(1), data.get(2));
//                }
//            }
//
//
//        }catch (Exception e) {
//            System.out.println("Erreur ! ");
//        }
//
//        return true;
//    }


        return null;
    }
}