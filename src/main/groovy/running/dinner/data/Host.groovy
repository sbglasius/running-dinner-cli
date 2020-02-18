package running.dinner.data

import com.fasterxml.jackson.annotation.JsonIgnore
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

    List<GuestGroup> entreCourseGuests = []
    List<GuestGroup> mainCourseGuests = []

    @JsonIgnore
    int getEntreCourseSeats() {
        guests.size() + (entreCourseGuests.sum { it.size } ?: 0)
    }

    @JsonIgnore
    int getMainCourseSeats() {
        guests.size() + (mainCourseGuests.sum { it.size } ?: 0)
    }

    String toString() {
        "${shortNames} ($maxGuests)"
    }
}
