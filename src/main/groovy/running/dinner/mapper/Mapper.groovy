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

            Map host = hostsInGroup.find { !it.vaertHjaelp }
            int max = host.maxGaester as int
            String address = host.vaertAdresse
            String serves = host.vaertServerer
            String foodDescription = host.vaertServererAndet
            boolean allergenes = host.jaTilAllergener
            List<Guest> guests = mapGuestsInGroup(hostsInGroup)
            boolean useMobilePay = host.mobilePayOK
            String mobilePay = host.mobilePayNummer
            return new Host(guests, max, address - ', Gl. Rye, 8680 Ry', useMobilePay ? mobilePay : null, serves, foodDescription, allergenes)
        }
        return new Hosts(hosts: allHosts)
    }

    private static List<Guest> mapGuestsInGroup(List<Map> guestsInGroup) {
        try {
            return guestsInGroup.collect {
                new Guest(it.navn as String, it.mobil as String, it.email as String, it.adresse, it.allergener, it.hensyn)
            }
        } catch (e) {
            println guestsInGroup
            throw e
        }
    }
}
