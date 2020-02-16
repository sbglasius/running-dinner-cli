package running.dinner.data

import groovy.transform.TupleConstructor

@TupleConstructor(includeSuperProperties = true)
class Host extends GuestGroup {
    int maxGuests
    String hostAddress
    String mobilePay
    String foodType
    String foodDescription
    boolean allergenes

    boolean getVegetar() {
        foodType == 'Vegetar mad'
    }

    Map<String, List<Guest>> courses = [:]

    int getEntres() {
        courses.entre.size()
    }
    int getMains() {
        courses.main.size()
    }

}
