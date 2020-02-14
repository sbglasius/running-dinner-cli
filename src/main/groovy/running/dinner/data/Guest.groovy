package running.dinner.data

import groovy.transform.TupleConstructor

@TupleConstructor
class Guest {
    String name
    String mobile
    String email
    String adresse

    String allergener
    String hensyn


    boolean getVegetar() {
        allergener == 'Vegetar'
    }

    boolean getHarAllergi() {
        allergener == 'Allergier eller andet'
    }
}
