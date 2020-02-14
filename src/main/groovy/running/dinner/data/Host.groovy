package running.dinner.data

import groovy.transform.TupleConstructor

@TupleConstructor(includeSuperProperties = true)
class Host extends GuestGroup {
    int maxGuests
    String hostAddress
    String foodType
    String foodDescription
    boolean allergenes

    boolean getVegetar() {
        foodType == 'Vegetar mad'
    }

    Map<String, List<Guest>> courses = [:]
}
