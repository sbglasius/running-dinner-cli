package running.dinner.cli

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import groovy.transform.Memoized
import groovy.util.logging.Slf4j
import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import running.dinner.data.GuestGroup
import running.dinner.data.Host
import running.dinner.flexbillet.FlexbilletService
import running.dinner.mapper.Mapper
import running.dinner.processor.GuestProcessor
import running.dinner.processor.GuestRandomizer

import javax.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
@Command(name = 'running-dinner-cli', description = '...',
        mixinStandardHelpOptions = true)
class RunningDinnerCliCommand implements Runnable {


    @Inject
    FlexbilletService fetchDataService

    @Option(names = ['-v', '--verbose'], description = 'Print reg json')
    boolean verbose

    @Option(names = ['-m', '--map'], description = 'Generate map data')
    boolean map

    @Option(names = ['-s'])
    boolean sort

    static void main(String[] args) throws Exception {
        PicocliRunner.run(RunningDinnerCliCommand.class, args)
    }

    void run() {
        List<Map> data = fetchDataService.fetchData()
//        if (verbose) {


            Map<String, Map<String, List<Map>>> sorted = GuestProcessor.sortGuests(data)
            List<GuestGroup> guests = Mapper.mapGuests(sorted['guests'])
            List<Host> hosts = Mapper.mapHosts(sorted['hosts'])

            log.debug mapper.writeValueAsString(guests)
            log.debug ('-'*80)
            GuestRandomizer.randomize(guests, hosts)
            log.debug mapper.writeValueAsString(hosts)
            log.debug ('-'*80)


//        }
        if(map) {
            generateMapData(data)
        }

    }

    void generateMapData(List<Map> data) {
        List<String> adresses = data.collect {
            String address = it.adresse
            if(!(address ==~ /.*\d{4}.*/)) {
                address += ', 8680 Ry'
            }
            return "${address.toLowerCase()}" as String
        }.unique()

        adresses.each {
            log.debug it
        }
    }

    @Memoized
    ObjectMapper getMapper() {
        JavaTimeModule module = new JavaTimeModule()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(formatter)
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(formatter)
        module.addSerializer(LocalDateTime, localDateTimeSerializer)
        module.addDeserializer(LocalDateTime, localDateTimeDeserializer)

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        mapper.registerModule(module)
        return mapper
    }


}
