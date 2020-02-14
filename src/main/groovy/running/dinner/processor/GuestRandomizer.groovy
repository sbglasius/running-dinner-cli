package running.dinner.processor

import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import running.dinner.data.Guest
import running.dinner.data.GuestGroup
import running.dinner.data.Host

import java.security.SecureRandom

@TupleConstructor
@Slf4j
class GuestRandomizer {
    List<GuestGroup> guestGroups
    List<Host> hosts
    Map<String, List<GuestGroup>> notAllocated = [:]

    private Random random = new SecureRandom()

    static GuestRandomizer randomize(List<GuestGroup> guestGroups, List<Host> hosts) {
        new GuestRandomizer(guestGroups, hosts).distributeGuestsOnHosts()
    }

    GuestRandomizer distributeGuestsOnHosts() {
        guestGroups.each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'entre')
        }
        guestGroups.reverse().each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'main', 'entre')
        }

        return this
    }

    void addGuestToHost(GuestGroup guestGroup, String course, String avoid = null) {
        boolean vegitarianGuests = guestGroup.guests.any { it.vegetar  }
        boolean allergeneGuests = guestGroup.guests.any { it.harAllergi }
        List<Host> availableHosts = hosts.findAll { Host host ->
            int guestsInGroup = host.courses[course]?.size() ?: 0
            List<Guest> avoidGuests = avoid ? host.courses[avoid] : []
            return host.vegetar == vegitarianGuests &&
                    host.allergenes == allergeneGuests &&
                    (host.maxGuests - host.guests.size()) >= guestsInGroup &&
                    !(guestGroup.guests.any { it in avoidGuests })
        }
        if(!availableHosts) {
            availableHosts = hosts.findAll { Host host ->
                int guestsInGroup = host.courses[course]?.size() ?: 0
                List<Guest> avoidGuests = avoid ? host.courses[avoid] : []
                return (host.maxGuests - host.guests.size()) >= guestsInGroup ?: 0 &&
                        !(guestGroup.guests.any { it in avoidGuests })
            } ?: []
        }
        log.debug "vegetarGaester: $vegitarianGuests allergeneGuests: $allergeneGuests antalVaerter: ${availableHosts.size()}"
        if(!availableHosts) {
            List<GuestGroup> missingAllocation = notAllocated[course] ?: []
            missingAllocation << guestGroup
            notAllocated[course] = missingAllocation
            return
        }
        Host host = availableHosts[random.nextInt(availableHosts.size())]

        if (!host.courses[course]) {
            host.courses[course] = host.guests.collect()
        }
        host.courses[course].addAll(guestGroup.guests)
    }
}
