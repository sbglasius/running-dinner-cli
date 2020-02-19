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
import running.dinner.data.Guest
import running.dinner.data.GuestGroup
import running.dinner.data.Hosts
import running.dinner.email.SendEmail
import running.dinner.flexbillet.FlexbilletService
import running.dinner.mapper.Mapper
import running.dinner.output.SpreadsheetOutput
import running.dinner.output.WordOutput
import running.dinner.processor.GuestProcessor
import running.dinner.processor.GuestRandomizer
import running.dinner.templates.MessageTemplates
import running.dinner.transfer.ExportImport

import javax.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
@Command(name = 'running-dinner-cli')
class RunningDinnerCliCommand implements Runnable {


    @Inject
    FlexbilletService fetchDataService

    @Inject
    SendEmail sendEmail

    @Option(names = ['-v', '--verbose'], description = 'Print reg json')
    boolean verbose = false

    @Option(names = ['-m', '--map'], description = 'Generate map data')
    boolean map

    @Option(names = ['--hostEmail'], description = 'Send first email to hosts')
    boolean hostEmail = false

    @Option(names = ['--guestEmail'], description = 'Send first email to guests')
    boolean guestEmail = false

    @Option(names = ['--documents'], description = 'Create documents')
    boolean documents = false

    static void main(String[] args) throws Exception {
        PicocliRunner.run(RunningDinnerCliCommand.class, args)
    }

    void run() {
        Hosts hosts = ExportImport.importData()
        if (!hosts) {

            log.debug("New data fetch")
            List<Map> data = fetchDataService.fetchData()
            log.debug("Alm mad: ${data.count { it.allergener == 'Ingen særlige hensyn' }}")
            log.debug("Vegetar: ${data.count { it.allergener == 'Vegetar' }}")
            log.debug("Veganer: ${data.count { it.allergener == 'Veganer' }}")
            log.debug("Allergi: ${data.count { it.allergener == 'Allergier eller andet' }}")
            log.debug("Alle   : ${data.size()}")
            GuestProcessor.preprocessGuests(data)
            GuestProcessor.groupSingles(data)
            Map<String, Map<String, List<Map>>> sorted = GuestProcessor.sortGuests(data)
            List<GuestGroup> guests = Mapper.mapGuests(sorted['guests'])
            hosts = Mapper.mapHosts(sorted['hosts'])
            log.debug("Pladser: ${hosts.hosts.sum { it.maxGuests }}")
            GuestRandomizer randomizer = GuestRandomizer.randomize(guests, hosts)
            log.debug(mapper.writeValueAsString(randomizer.notAllocated))
            hosts.notAllocated = randomizer.notAllocated
            ExportImport.exportData(hosts)
        }

        if (verbose) {
            hosts.hosts.each { host ->
                println('-' * 80)
                println "-- Værter: ${host.shortNames} ${host.vegetar ? " (vegetar) " : ''} : ${host.maxGuests}"
                println "-- Til forret: ${host.entreCourseSeats}"
                printCourse(host.entreCourseGuests)
                println "-- Til hovedret: ${host.mainCourseSeats}"
                printCourse(host.mainCourseGuests)
                println "-- Udbetal ${host.mobilePay ? "til MobilePay: $host.mobilePay" : 'konto'}"
            }
        }
        if (true) {
            generateMapData(hosts.hosts*.hostAddress)
        }


        if (hostEmail) {
            hosts.hosts.each { host ->
                sendEmail.simpleMail("Running Dinner", MessageTemplates.createHostEmail(host), *host.guests)
            }

        }
        if (guestEmail) {
            hosts.hosts.each { host ->
                host.entreCourseGuests.each { guestGroup ->
                    if (guestGroup.guests.any { it.single }) {
                        guestGroup.guests.each {
                            GuestGroup single = new GuestGroup(guests: [it])
                            sendEmail.simpleMail("Running Dinner", MessageTemplates.createGuestMail(host, single), it)
                        }
                    } else {
                        sendEmail.simpleMail("Running Dinner", MessageTemplates.createGuestMail(host, guestGroup), *guestGroup.guests)

                    }
                }
            }
        }
        if (documents) {
            SpreadsheetOutput.buildSpreadsheet(hosts)
            WordOutput.hostWineInformation(hosts)
            WordOutput.hostEnvelopeWithPostcards(hosts)
            WordOutput.guestsPostcards(hosts)
        }

        // Sanity check
        log.debug("Entre seats: ${hosts.hosts.sum { it.entreCourseSeats }}")
        log.debug("Main  seats: ${hosts.hosts.sum { it.mainCourseSeats }}")
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

    void generateMapData(List<String> adresses) {
        adresses.unique().each {
            println "${it - 'Gl. Rye'} Gammel Rye, 8680 Ry"
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
