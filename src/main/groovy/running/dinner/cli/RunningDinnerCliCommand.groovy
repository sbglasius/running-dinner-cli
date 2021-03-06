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
import running.dinner.data.Host
import running.dinner.data.Hosts
import running.dinner.email.SendEmail
import running.dinner.flexbillet.FlexbilletService
import running.dinner.mapper.Mapper
import running.dinner.output.SpreadsheetOutput
import running.dinner.output.WordOutput
import running.dinner.processor.GuestProcessor
import running.dinner.processor.GuestRandomizer
import running.dinner.sms.SendSms
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

    @Inject
    SendSms sendSms

    @Option(names = ['-v', '--verbose'], description = 'Print reg json')
    boolean verbose = false

    @Option(names = ['-m', '--map'], description = 'Generate map data')
    boolean map = false

    @Option(names = ['--teaserEmail'], description = 'Send teaser email to everybody')
    boolean teaserEmail = false

    @Option(names = ['--hostEmail'], description = 'Send first email to hosts')
    boolean hostEmail = false

    @Option(names = ['--helperEmail'], description = 'Send helper email to guests')
    boolean helperEmail = false

    @Option(names = ['--guestEmail'], description = 'Send first email to guests')
    boolean guestEmail = false

    @Option(names = ['--sheet'], description = 'Create spreadsheet')
    boolean spreadsheet = false

    @Option(names = ['--documents'], description = 'Create documents')
    boolean documents = false

    @Option(names = ['--sms'], description ='Send sms')
    boolean sms = true

    static void main(String[] args) throws Exception {
        PicocliRunner.run(RunningDinnerCliCommand.class, args)
    }

    void run() {
        Hosts hosts = ExportImport.importData()
        List<Map> data = fetchDataService.fetchData()
        if (!hosts) {

            log.debug("New data fetch")
            data = fetchDataService.fetchData()
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
        hosts.getEverybody()*.guests.flatten().each { Guest guest ->
            Map d = data.find { it.navn == guest.name }
            guest.helper = d.hjaelper ?: false
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
        if (map) {
            SpreadsheetOutput.buildAddresses(hosts)
        }


        if (teaserEmail) {
            hosts.everybody.takeRight(3).each { guestGroup ->
                sendEmail.simpleMail("Running Dinner", MessageTemplates.createTeaserMail(guestGroup), *guestGroup.guests)
            }

        }

        if (helperEmail) {
            hosts.allGuests*.guests.flatten().findAll { it.helper }.each { Guest guest ->
                sendEmail.simpleMail("Running Dinner", MessageTemplates.createHelperMail(guest), guest)

            }
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
        if(1==2) {
            hosts.hosts.find { it.hostAddress.startsWith('Hedelundvej 15')}.mainCourseGuests.each {
                sendEmail.simpleMail("Running Dinner - VIGTIGT!", MessageTemplates.createCorrection(it), *it.guests)
            }
        }

        if (spreadsheet) {
            SpreadsheetOutput.buildSpreadsheet(hosts)
        }
        if (documents) {
            WordOutput.hostWineInformation(hosts)
            WordOutput.hostEnvelopeWithPostcards(hosts)
            WordOutput.guestsPostcards(hosts)
        }
        if(sms) {
//            List<String> mobiles = hosts.hosts.guests.flatten()*.mobile.unique()
//            mobiles = ['40449188',*mobiles]
//            sendSms.sendMessage("""\
//                Hej Running Dinner værter
//
//                Så er vinen, som er sponseret af Byens Egen Butik pakket og klar til afhentning. Når du henter den, så husk også kuverten som skal bruges den 7. marts. Bemærk, at der er navn på hver kuvert!
//
//                Vi glæder os til at holde fest med jer!
//
//                De bedste hilsner
//                Running Dinner udvalget i
//                Brugsens bestyrelse
//                """.stripIndent(), *mobiles)
//            sendSms.sendTimedMessage("Gå i seng", LocalDateTime.of(2020,3,3,23,34), '40449188')
            hosts.hosts.each { host ->
                host.entreCourseGuests.each { guestGroup ->
                    Host nextHost = hosts.findMainCourseHost(guestGroup)
                    guestGroup.guests.each { guest ->
                        LocalDateTime schedule = LocalDateTime.of(2020, 3, 7, 17, 30)
                        println ">> sendes ${schedule} ${guest.mobile}".padRight(80, '-')
                        println MessageTemplates.createTeaserSMS(host, guest)
//                        schedule = LocalDateTime.of(2020, 3, 7, 19, 35)
//                        println ">> sendes ${schedule} ${guest.mobile}".padRight(80, '-')
//                        println MessageTemplates.createMainCourseSMS(nextHost, guest)
//                        schedule = LocalDateTime.of(2020, 3, 7, 21, 35)
//                        println ">> sendes ${schedule} ${guest.mobile}".padRight(80, '-')
//                        println MessageTemplates.createEndOfCoursesSMS(guest)
//                        schedule = LocalDateTime.of(2020, 3, 8, 8, 30)
//                        println ">> sendes ${schedule} ${guest.mobile}".padRight(80, '-')
//                        println MessageTemplates.createCleanupSMS(guest)
                    }

                }
            }
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
