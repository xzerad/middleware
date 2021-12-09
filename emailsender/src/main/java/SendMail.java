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

         e = Dotenv.configure().directory("/home/alaeddine/IdeaProjects/midd/assets").filename(".env").load();
    }

    public  void send() throws EmailException {

        Email email = new SimpleEmail();
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(e.get("email"),e.get("password")));
        email.setSSLOnConnect(true);
        email.setFrom("alaeddinehmidazbot@gmail.com");
        email.setSubject("resultat");
        email.setMsg(msg);
        email.addTo(valeur);
        email.send();
    }
}
