package running.dinner.data

class Hosts {
    List<Host> hosts

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

    List<GuestGroup> getAllGuests() {
        hosts*.entreCourseGuests.flatten().sort { it.shortNames } as List<GuestGroup>
    }
}
