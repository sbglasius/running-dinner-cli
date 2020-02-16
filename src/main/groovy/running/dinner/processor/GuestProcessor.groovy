package running.dinner.processor

import groovy.util.logging.Slf4j

@Slf4j
class GuestProcessor {
    static Map<String, Map> koebsrefMapping = [
            '1006556': [koebsref: '1007383', vaertHjaelp: true],
            '1006444': [koebsref: '1007427', vaertHjaelp: true]
    ]

    static Map<String, Map<String, List<Map>>> sortGuests(List<Map> unsorted) {
        unsorted.groupBy { it.maxGaester ? 'hosts' : 'guests' }
                .collectEntries { String key, List<Map> guests ->
                    Map groupedGuests = guests.groupBy { it.koebsref }
                    [key, groupedGuests]
                } as Map<String, Map<String, List<Map>>>
    }

    /**
     * Link specific guests
     * @param unsorted
     * @return
     */
    static void preprocessGuests(List<Map> unsorted) {
        unsorted.each { Map<String, Serializable> row ->
            koebsrefMapping[row.koebsref as String]?.each { key, value ->
                row[key] = value
            }
        }
    }

    static void groupSingles(List<Map> guests) {
        List<Map> singles = guests.groupBy { it.koebsref }.findAll { it.value.size() == 1 }.collect { it.value }.flatten()
        List<Map> singlesVegetar = singles.findAll { it.test }
        shuffleSingles(singles)
    }

    private static void shuffleSingles(List<Map> singles) {
        Collections.shuffle(singles)
        singles.collate(2).each {
            if (it.size() == 2) {
                it[1].koebsref = it[0].koebsref
            }
        }
    }
}
