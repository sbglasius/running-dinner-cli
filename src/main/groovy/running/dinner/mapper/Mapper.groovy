package running.dinner.mapper

import running.dinner.data.Guest
import running.dinner.data.GuestGroup
import running.dinner.data.Host

class Mapper {
    static List<GuestGroup> mapGuests(Map guestsMap) {
        guestsMap.collect { key, guestsInGroup ->
            List<Guest> guests = mapGuestsInGroup(guestsInGroup as Map)
            return new GuestGroup(guests)
        }
    }

    static List<Host> mapHosts(Map<String, List<Map>> hostMap) {
        hostMap.collect { key, hostsInGroup ->
            int max = hostsInGroup[0].maxGaester as int
            String address = hostsInGroup[0].vaertAdresse
            String serves = hostsInGroup[0].vaertServerer
            String foodDescription = hostsInGroup[0].vaertServererAndet
            boolean allergenes = hostsInGroup[0].jaTilAllergener
            List<Guest> guests = mapGuestsInGroup(hostsInGroup as Map)
            return new Host(guests, max, address, serves, foodDescription, allergenes)
        }
    }

    private static List<Guest> mapGuestsInGroup(Map guestsInGroup) {
        return guestsInGroup.collect {
            new Guest(it.navn as String, it.mobil as String, it.email as String)
        }
    }
}
