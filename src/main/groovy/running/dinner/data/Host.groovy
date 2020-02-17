package running.dinner.data

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@TupleConstructor(includeSuperProperties = true)
class Host extends GuestGroup {
    int maxGuests
    String hostAddress
    String mobilePay
    String foodType
    String foodDescription
    boolean allergenes

    @JsonIgnore
    boolean getVegetar() {
        foodType == 'Vegetar mad'
    }

    Map<String, List<Guest>> courses = [:]

    @JsonIgnore
    int getEntres() {
        courses.entre.size()
    }

    @JsonIgnore
    int getMains() {
        courses.main.size()
    }

    String toString() {
        "${shortNames} ($maxGuests)"
    }
}
