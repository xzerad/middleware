import org.apache.commons.mail.EmailException;

public class test {

    public static void main(String[] args) throws EmailException {
        SendMail s = new SendMail("bonjour","soniabahri417@gmail.com");
        s.send();
    }
}
