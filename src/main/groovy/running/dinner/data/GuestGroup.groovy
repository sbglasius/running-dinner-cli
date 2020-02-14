package running.dinner.data

import groovy.transform.TupleConstructor

@TupleConstructor
class GuestGroup {
    List<Guest> guests = []

    Integer getSize() {
        guests.size()
    }

    boolean getFaellesAdresse() {
        guests.every { it.adresse.toLowerCase() == guests[0].adresse.toLowerCase() }
    }
}
