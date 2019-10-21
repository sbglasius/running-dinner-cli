package running.dinner.data

import groovy.transform.TupleConstructor

@TupleConstructor
class GuestGroup {
    List<Guest> guests = []

    Integer getSize() {
        guests.size()
    }
}
