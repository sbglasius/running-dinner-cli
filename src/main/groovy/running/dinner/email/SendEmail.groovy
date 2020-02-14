package running.dinner.email

import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendEmail {
    static void simpleMail(String to, String subject, String body) throws Exception {
        File f = new File('/home/sbglasius/.groovy/running-dinner.properties')

        Properties props = System.getProperties()
        f.withInputStream {
            props.load(it)
        }

        String host = props.get('mail.smtp.host')
        String user = props.get('mail.smtp.username')
        String password = props.get('mail.smtp.password')
        String name = props.get('mail.smtp.fromName')
        String from = props.get('mail.smtp.from')

        Session session = Session.getDefaultInstance(props, null)
        MimeMessage message = new MimeMessage(session)
        message.setFrom(new InternetAddress(from, name))
        message.setReplyTo([new InternetAddress(from)] as Address[])

        // Override email adresse.
        to = "soeren+running-dinner@glasius.dk"

        InternetAddress toAddress = new InternetAddress(to)

        message.addRecipient(Message.RecipientType.TO, toAddress)
//        message.addRecipient(Message.RecipientType.BCC, new InternetAddress('info@runningdinner.nu'))
        message.setSubject(subject)
        message.setText(body)

        Transport transport = session.getTransport("smtp")

        transport.connect(host, user, password)

        transport.sendMessage(message, message.getAllRecipients())
        transport.close()
    }
}
