package running.dinner.data

class Hosts {
    List<Host> hosts

    Host findMainCourseHost(GuestGroup guestGroup) {
        hosts.find { host ->
            guestGroup.names in host.mainCourseGuests*.names
        }
    }
}
