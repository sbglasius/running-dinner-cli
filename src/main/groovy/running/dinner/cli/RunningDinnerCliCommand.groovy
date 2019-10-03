package running.dinner.cli

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import groovy.json.JsonBuilder
import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import running.dinner.FlexbilletService

import javax.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Command(name = 'running-dinner-cli', description = '...',
        mixinStandardHelpOptions = true)
class RunningDinnerCliCommand implements Runnable {

    @Inject
    FlexbilletService fetchDataService

    @Option(names = ['-v', '--verbose'], description = '...')
    boolean verbose

    static void main(String[] args) throws Exception {
        PicocliRunner.run(RunningDinnerCliCommand.class, args)
    }

    void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(formatter)
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(formatter)

        JavaTimeModule module = new JavaTimeModule()
        module.addSerializer(LocalDateTime, localDateTimeSerializer)
        module.addDeserializer(LocalDateTime, localDateTimeDeserializer)

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)//.registerModules(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule())
        mapper.registerModule(module)
        String jsonData = mapper.writeValueAsString(fetchDataService.fetchData())
        println jsonData
    }
}
