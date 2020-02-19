package running.dinner.data

import com.fasterxml.jackson.annotation.JsonIgnore

class Hosts {
    List<Host> hosts


    Map<String, List<GuestGroup>> notAllocated

    Host findEntreCourseHost(GuestGroup guestGroup) {
        hosts.find { host ->
            guestGroup.names in host.entreCourseGuests*.names
        }
    }
    Host findMainCourseHost(GuestGroup guestGroup) {
        hosts.find { host ->
            guestGroup.names in host.mainCourseGuests*.names
        }
    }

    @JsonIgnore
    List<GuestGroup> getAllGuests() {
        hosts*.entreCourseGuests.flatten().sort { it.shortNames } as List<GuestGroup>
    }
}
