package running.dinner.sms

import groovy.util.logging.Slf4j

import javax.inject.Inject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Slf4j
class SendSms {
    @Inject
    SmsClient smsClient

    void sendMessage(String message, String... recipients) {
        List<Map> msisdns = createRecipients(recipients)

        Map response = smsClient.sendMessage('RD Gl. Rye'.take(11), message, msisdns)

        log.debug("SMS response: $response")
    }

    void sendTimedMessage(String message, LocalDateTime sendTime, String... recipients) {
        List<Map> msisdns = createRecipients(recipients)
        ZoneId zone = ZoneId.of("Europe/Copenhagen")
        ZoneOffset zoneOffset = zone.rules.getOffset(sendTime)
        Map response = smsClient.sendMessage('RD Gl. Rye'.take(11), message, sendTime.toEpochSecond(zoneOffset), msisdns)
        log.debug("SMS response: $response")

    }

    private List<Map> createRecipients(String... recipients) {
        List<Map> msisdns = recipients.collect { [msisdn: ('45' + it).toLong()] }
        return msisdns
    }
}
