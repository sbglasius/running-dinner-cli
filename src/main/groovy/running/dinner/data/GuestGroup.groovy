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

    String getShortNames() {
        List<String> names = guests*.name
        String lastName = names[-1].split(/ /)[-1]
        List<String> noLastNames = names.collect { (it - lastName).trim() }
        return "${noLastNames[0..-2].join(', ')} og ${names[-1]}"
    }

    String getNames() {
        List<String> names = guests*.name
        return names[0..-2].join(', ') + ' og ' + names[-1]
    }

}
