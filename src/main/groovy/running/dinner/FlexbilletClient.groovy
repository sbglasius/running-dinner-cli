package running.dinner

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client('${flexbillet.host}')
interface FlexbilletClient {

    @Get('/organizerservices/api/v1/generatereport?outputtoken=${flexbillet.token}&passphrase=${flexbillet.passphrase}')
    Map getData()

}
