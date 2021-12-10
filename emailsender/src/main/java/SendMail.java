import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.kafka.common.protocol.types.Field;

public class SendMail {
    String msg;
    String valeur;
    Dotenv e;
    public SendMail(String m, String v) {
         msg=m;
         valeur=v;
         e = Dotenv.configure().directory("C:\\Users\\sonia bahri\\Desktop\\mid\\middleware\\email_reader\\.env").filename(".env").load();
    }

    public  void send() throws EmailException {
        Email email = new SimpleEmail();
        email.setDebug(true);
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
        System.out.println(e.get("MAIL"));
        email.setAuthenticator(new DefaultAuthenticator(e.get("MAIL"),e.get("PASS")));
        email.setSSLOnConnect(true);
        email.setFrom(e.get("MAIL"));
        email.setSubject("resultat");
        email.setMsg(msg);
        email.addTo(valeur);
        email.send();
    }
}
