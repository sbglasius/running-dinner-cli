package running.dinner.mapper

import groovy.util.logging.Slf4j
import running.dinner.data.Guest
import running.dinner.data.GuestGroup
import running.dinner.data.Host

@Slf4j
class Mapper {
    static List<GuestGroup> mapGuests(Map guestsMap) {
        guestsMap.collect { key, guestsInGroup ->
            List<Guest> guests = mapGuestsInGroup(guestsInGroup as Map)
            return new GuestGroup(guests)
        }
    }

    static List<Host> mapHosts(Map<String, List<Map>> hostMap) {
        hostMap.collect { key, hostsInGroup ->
            Map host = hostsInGroup.find { !it.vaertHjaelp }
            int max = host.maxGaester as int
            String address = host.vaertAdresse
            String serves = host.vaertServerer
            String foodDescription = host.vaertServererAndet
            boolean allergenes = host.jaTilAllergener
            List<Guest> guests = mapGuestsInGroup(hostsInGroup as Map)
            boolean useMobilePay = host.mobilePayOK
            String mobilePay = host.mobilePayNummer
            return new Host(guests, max, address, useMobilePay ? mobilePay : null, serves, foodDescription, allergenes, [entre: guests.collect(), main: guests.collect()])
        }
    }

    private static List<Guest> mapGuestsInGroup(Map guestsInGroup) {
        return guestsInGroup.collect {
            new Guest(it.navn as String, it.mobil as String, it.email as String, it.adresse, it.allergener, it.hensyn)
        }
    }
}
