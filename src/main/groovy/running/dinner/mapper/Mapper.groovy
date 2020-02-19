package running.dinner.mapper

import groovy.util.logging.Slf4j
import running.dinner.data.Guest
import running.dinner.data.GuestGroup
import running.dinner.data.Host
import running.dinner.data.Hosts

@Slf4j
class Mapper {
    static List<GuestGroup> mapGuests(Map guestsMap) {
        guestsMap.collect { key, guestsInGroup ->
            List<Guest> guests = mapGuestsInGroup(guestsInGroup)
            return new GuestGroup(guests)
        }
    }

    static Hosts mapHosts(Map<String, List<Map>> hostMap) {
        List<Host> allHosts = hostMap.collect { key, hostsInGroup ->

            Map host = hostsInGroup.find { !it.hostHelper }
            int max = host.maxGaester as int
            String address = host.vaertAdresse
            String serves = host.vaertServerer
            String foodDescription = host.vaertServererAndet
            boolean allergenes = host.jaTilAllergener
            List<Guest> hosts = mapGuestsInGroup(hostsInGroup.findAll { !it.hostHelper })
            List<Map> helpersOnly = hostsInGroup.findAll { it.hostHelper }
            List<GuestGroup> guests = helpersOnly ? [new GuestGroup(mapGuestsInGroup(helpersOnly))]:[]
            boolean useMobilePay = host.mobilePayOK
            String mobilePay = host.mobilePayNummer
            return new Host(hosts, max, address - ', 8680 Ry', useMobilePay ? mobilePay : null, serves, foodDescription, allergenes, guests.collect(), guests.collect())
        }
        return new Hosts(hosts: allHosts)
    }

    private static List<Guest> mapGuestsInGroup(List<Map> guestsInGroup) {
        try {
            return guestsInGroup.collect {
                new Guest(it.navn as String, it.mobil as String, it.email as String, it.adresse, it.allergener, it.hensyn, it.single ?: false)
            }
        } catch (e) {
            println guestsInGroup
            throw e
        }
    }
}
