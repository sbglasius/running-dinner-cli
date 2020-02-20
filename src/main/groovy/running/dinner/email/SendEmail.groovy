package running.dinner.email

import groovy.util.logging.Slf4j
import running.dinner.data.Guest

import javax.inject.Singleton
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Singleton
@Slf4j
class SendEmail {

    Session session
    Transport transport

    Properties props

    InternetAddress fromAddress

    SendEmail() {
//        File config = new File('/home/sbglasius/.groovy/running-dinner.properties')

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
        MimeMessage message = new MimeMessage(session)
        message.setFrom(fromAddress)
        message.setReplyTo([fromAddress] as Address[])

        // Override email adresse.
//        to = [new Guest("Søren", "40449188", "soeren+running-dinner@glasius.dk")]
        to.each {
            InternetAddress toAddress = new InternetAddress(it.email, it.name)

            message.addRecipient(Message.RecipientType.TO, toAddress)
        }
        log.debug "Sender email til ${to*.name.join(' og ')}"
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress('info@runningdinner.nu', "Running Dinner"))
        message.setSubject(subject)
        message.setText(body)
        sleep(75)

        transport.sendMessage(message, message.getAllRecipients())
    }
}
