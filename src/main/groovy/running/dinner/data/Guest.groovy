package running.dinner.data

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.TupleConstructor

@TupleConstructor
class Guest {
    String name
    String mobile
    String email
    String adresse

    String allergener
    String hensyn

    boolean single = false

    @JsonIgnore
    boolean getVegetar() {
        allergener == 'Vegetar' || allergener == 'Veganer'
    }

    @JsonIgnore
    boolean getVeganer() {
        allergener == 'Veganer'
    }

    @JsonIgnore
    boolean getAllergy() {
        allergener == 'Allergier eller andet'
    }

    String toString() {
        name
    }
}
