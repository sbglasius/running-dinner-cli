package running.dinner.processor

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.convert.format.MapFormat

@ConfigurationProperties('guests')
class GuestPreprocessConfig {

    @MapFormat(transformation = MapFormat.MapTransformation.FLAT)
    Map<String, Map> preprocess = [:]
}
