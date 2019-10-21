package running.dinner.processor

class GuestProcessor {
    static Map<String, Map<String, List<Map>>> sortGuests(List<Map> unsorted) {
        unsorted.groupBy { it.maxGaester ? 'hosts':'guests'}
                .collectEntries { String key, List<Map> guests ->
                    Map groupedGuests = guests.groupBy { it.koebsref }
                    [key, groupedGuests]
        } as Map<String, Map<String, List<Map>>>
    }
}
