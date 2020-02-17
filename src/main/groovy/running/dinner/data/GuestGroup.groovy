package running.dinner.data

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.TupleConstructor

@TupleConstructor
class GuestGroup {
    List<Guest> guests = []

    @JsonIgnore
    Integer getSize() {
        guests.size()
    }

    @JsonIgnore
    boolean getFaellesAdresse() {
        guests.every { it.adresse.toLowerCase() == guests[0].adresse.toLowerCase() }
    }

    @JsonIgnore
    String getShortNames() {
        List<String> names = guests*.name
        String lastName = names[-1].split(/ /)[-1]
        List<String> noLastNames = names.collect {
            if(it.endsWith(lastName)) {
                return (it - lastName).trim()
            } else {
            return it}
        }
        return "${noLastNames[0..-2].join(', ')} og ${names[-1]}"
    }

    @JsonIgnore
    String getNames() {
        List<String> names = guests*.name
        return names[0..-2].join(', ') + ' og ' + names[-1]
    }

}
