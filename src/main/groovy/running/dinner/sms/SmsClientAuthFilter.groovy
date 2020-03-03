package running.dinner.sms

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import org.reactivestreams.Publisher

@Filter('/rest/mtsms/**')
class SmsClientAuthFilter implements HttpClientFilter{
    String token

    SmsClientAuthFilter(@Property(name = 'gatewayapi.token') token) {
        this.token = token
    }

    @Override
    Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
        String basic = Base64.encoder.encodeToString("${token}:".bytes)
        request.header('Authorization', "Basic ${basic}")
        return chain.proceed(request)
    }
}
