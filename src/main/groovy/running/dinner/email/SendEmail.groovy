package running.dinner.email

import running.dinner.data.Guest

import javax.inject.Singleton
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Singleton
class SendEmail {

    Session session
    Transport transport

    Properties props

    InternetAddress fromAddress
    SendEmail() {
        File config = new File('/home/sbglasius/projects/running-dinner/running-dinner-cli/docker/greenmail.properties')

        props = System.getProperties()
        config.withInputStream {
            props.load(it)
        }

        String host = props.get('mail.smtp.host')
        String user = props.get('mail.smtp.username')
        String password = props.get('mail.smtp.password')
        String name = props.get('mail.smtp.fromName')
        String from = props.get('mail.smtp.from')

        session = Session.getDefaultInstance(props, null)
        fromAddress = new InternetAddress(from, name)
        transport = session.getTransport("smtp")

        transport.connect(host, user, password)

    }


    void simpleMail(String subject, String body, Guest... to) throws Exception {
//        File config = new File('/home/sbglasius/.groovy/running-dinner.properties')
        MimeMessage message = new MimeMessage(session)
        message.setFrom(fromAddress)
        message.setReplyTo([fromAddress] as Address[])

        // Override email adresse.
        //to = "soeren+running-dinner@glasius.dk"
        to.each {
            InternetAddress toAddress = new InternetAddress(it.email, it.name)

            message.addRecipient(Message.RecipientType.TO, toAddress)
        }
//        message.addRecipient(Message.RecipientType.BCC, new InternetAddress('info@runningdinner.nu'))
        message.setSubject(subject)
        message.setText(body)

        transport.sendMessage(message, message.getAllRecipients())
    }
}
