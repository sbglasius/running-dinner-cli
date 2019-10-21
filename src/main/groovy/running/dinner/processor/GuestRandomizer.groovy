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

    private Random random = new SecureRandom()

    static void randomize(List<GuestGroup> guestGroups, List<Host> hosts) {
        new GuestRandomizer(guestGroups, hosts).distributeGuestsOnHosts()
    }

    void distributeGuestsOnHosts() {
        guestGroups.each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'entre')
        }
        guestGroups.reverse().each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'main', 'entre')
        }
    }

    void addGuestToHost(GuestGroup guestGroup, String course, String avoid = null) {
        List<Host> availableHosts = hosts.findAll {
            int guestsInGroup = it.courses[course]?.size() ?: 0
            List<Guest> avoidGuests = avoid ? it.courses[avoid] : []
            return it.maxGuests >= guestsInGroup ?: 0 && !(guestGroup.guests.any { it in avoidGuests })
        }
        log.debug "Available hosts: ${availableHosts*.guests*.name}"
        if(!availableHosts) return
        Host host = availableHosts[random.nextInt(availableHosts.size())]

        if (!host.courses[course]) {
            host.courses[course] = host.guests.collect()
        }
        host.courses[course].addAll(guestGroup.guests)

    }
}
