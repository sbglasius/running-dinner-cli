package running.dinner.processor

import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import running.dinner.data.GuestGroup
import running.dinner.data.Host
import running.dinner.data.Hosts

import java.security.SecureRandom

@TupleConstructor
@Slf4j
class GuestRandomizer {
    List<GuestGroup> guestGroups
    Hosts hosts
    Map<String, List<GuestGroup>> notAllocated = [:]

    private Random random = new SecureRandom()

    static GuestRandomizer randomize(List<GuestGroup> guestGroups, Hosts hosts) {
        new GuestRandomizer(guestGroups, hosts).distributeGuestsOnHosts()
    }

    GuestRandomizer distributeGuestsOnHosts() {
        guestGroups.findAll { it.guests.any { it.vegetar}}.each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'entre')
        }
        guestGroups.findAll { !(it.guests.any { it.vegetar }) }.each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'entre')
        }
        guestGroups.findAll { it.guests.any { it.vegetar } }.each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'main', 'entre')
        }
        guestGroups.findAll { !(it.guests.any { it.vegetar }) }.each { GuestGroup guestGroup ->
            addGuestToHost(guestGroup, 'main', 'entre')
        }

        return this
    }

    void addGuestToHost(GuestGroup guestGroup, String course, String avoid = null) {
        boolean vegitarianGuests = guestGroup.guests.any { it.vegetar }
        boolean allergeneGuests = guestGroup.guests.any { it.allergy }
        List<Host> availableHosts = hosts.hosts.findAll { Host host ->
            int guestsInGroup = course == 'entre' ? host.entreCourseSeats : host.mainCourseSeats
            List<GuestGroup> avoidGuests = avoid == 'entre' ?  host.entreCourseGuests : []
            return host.vegetar == vegitarianGuests &&
                    host.allergenes == allergeneGuests &&
                    guestGroup.size <= (host.maxGuests - guestsInGroup) &&
                    !(guestGroup.any { it in avoidGuests })
        }
        if (!availableHosts) {
            availableHosts = hosts.hosts.findAll { Host host ->
                int guestsInGroup = course == 'entre' ? host.entreCourseSeats : host.mainCourseSeats
                List<GuestGroup> avoidGuests = avoid == 'entre' ? host.entreCourseGuests : []
                return guestGroup.size <= (host.maxGuests - guestsInGroup) &&
                        !(guestGroup.any { it in avoidGuests })
            } ?: []
        }
//        log.debug "vegetarGaester: $vegitarianGuests allergeneGuests: $allergeneGuests antalVaerter: ${availableHosts.size()} "
        if (!availableHosts) {
            List<GuestGroup> missingAllocation = notAllocated[course] ?: []
            missingAllocation << guestGroup
            notAllocated[course] = missingAllocation
            return
        }
        Host host = availableHosts[random.nextInt(availableHosts.size())]

        (course == 'entre' ? host.entreCourseGuests : host.mainCourseGuests) << guestGroup
    }
}
