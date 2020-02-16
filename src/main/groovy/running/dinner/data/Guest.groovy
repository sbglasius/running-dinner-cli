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
        allergener == 'Vegetar' || allergener == 'Veganer'
    }

    boolean getVeganer() {
        allergener == 'Veganer'
    }

    boolean getAllergy() {
        allergener == 'Allergier eller andet'
    }
}
