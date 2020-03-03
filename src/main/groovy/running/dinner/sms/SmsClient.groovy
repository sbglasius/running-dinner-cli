package running.dinner.sms


import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client('${gatewayapi.host}')
interface SmsClient {
    @Post('/rest/mtsms')
    Map sendMessage(String sender, String message, List<Map> recipients)

    @Post('/rest/mtsms')
    Map sendMessage(String sender, String message, Long sendtime, List<Map> recipients)
}
