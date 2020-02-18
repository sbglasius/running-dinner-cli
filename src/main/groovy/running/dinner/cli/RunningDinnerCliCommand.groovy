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
import running.dinner.output.SpreadsheetOutput
import running.dinner.output.WordOutput
import running.dinner.transfer.ExportImport
import running.dinner.data.Guest
import running.dinner.data.GuestGroup
import running.dinner.data.Host
import running.dinner.flexbillet.FlexbilletService
import running.dinner.mapper.Mapper
import running.dinner.processor.GuestProcessor
import running.dinner.processor.GuestRandomizer
import running.dinner.templates.MessageTemplates

import javax.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
@Command(name = 'running-dinner-cli')
class RunningDinnerCliCommand implements Runnable {


    @Inject
    FlexbilletService fetchDataService

    @Option(names = ['-v', '--verbose'], description = 'Print reg json')
    boolean verbose = true

    @Option(names = ['-m', '--map'], description = 'Generate map data')
    boolean map

    @Option(names = ['--hostEmail'], description = 'Send first email to hosts')
    boolean hostEmail = true

    static void main(String[] args) throws Exception {
        PicocliRunner.run(RunningDinnerCliCommand.class, args)
    }

    void run() {
        List<Host> hosts = ExportImport.importData()
        if (!hosts) {

            log.debug("New data fetch")
            List<Map> data = fetchDataService.fetchData()
            log.debug("Alm mad: ${data.count { it.allergener == 'Ingen særlige hensyn' }}")
            log.debug("Vegetar: ${data.count { it.allergener == 'Vegetar' }}")
            log.debug("Veganer: ${data.count { it.allergener == 'Veganer' }}")
            log.debug("Allergi: ${data.count { it.allergener == 'Allergier eller andet' }}")

//        if (verbose) {
            GuestProcessor.preprocessGuests(data)
            GuestProcessor.groupSingles(data)
            Map<String, Map<String, List<Map>>> sorted = GuestProcessor.sortGuests(data)
            List<GuestGroup> guests = Mapper.mapGuests(sorted['guests'])
            hosts = Mapper.mapHosts(sorted['hosts'])
            GuestRandomizer randomizer = GuestRandomizer.randomize(guests, hosts)
            log.debug(mapper.writeValueAsString(randomizer.notAllocated))
            ExportImport.exportData(hosts)
        }

        if (verbose) {
            hosts.each { host ->
                println('-' * 80)
                println "-- Værter: ${host.shortNames} ${host.vegetar ? " (vegetar) " : ''}"
                println "-- Til forret: ${host.entreCourseSeats}"
                printCourse(host.entreCourseGuests)
                println "-- Til hovedret: ${host.mainCourseSeats}"
                printCourse(host.mainCourseGuests)
                println "-- Udbetal ${host.mobilePay ? "til MobilePay: $host.mobilePay" : 'konto'}"

            }
        }
        if (map) {
            generateMapData(data)
        }

        if (hostEmail) {
            hosts.each { host ->
                println('-' * 80)
                println MessageTemplates.createHostEmail(host)
            }
        }
        SpreadsheetOutput.buildSpreadsheet(hosts)
        WordOutput.hostWineInformation(hosts)
        WordOutput.hostEnvelopeWithPostcards(hosts)
        WordOutput.guestsPostcards(hosts)

    }

    void printCourse(List<GuestGroup> guestGroup) {
        List<Guest> guests = guestGroup*.guests.flatten()
        guests.each {
            println "--   ${it.name} ${it.vegetar ? " (vegetar)" : ''}"
        }
        if (guests.any { it.hensyn }) {
            println "--   Hensyn til allergener:"
            guests.findAll { it.hensyn }.each {
                println "--      ${it.hensyn}"
            }
        }
    }

    void generateMapData(List<Map> data) {
        List<String> adresses = data.collect {
            String address = it.adresse
            if (!(address ==~ /.*\d{4}.*/)) {
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
