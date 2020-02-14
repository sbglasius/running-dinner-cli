package running.dinner.flexbillet

import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton
import java.time.LocalDateTime
import java.time.ZoneId

@Slf4j
@Singleton
class FlexbilletService {

    @Inject
    FlexbilletClient flexbilletClient

    List<Map> fetchData() {
        Map data = flexbilletClient.getData()


        Map<Integer, Map> columns = mapColumns(data.report.columns.column as List<Map>)

        List<Map> extractedData = extractData(columns, data.report.rows.row*.entry as List<List<Map>>)
        return extractedData
    }

    private List<Map> extractData(Map<Integer, Map> columns, List<List<Map>> rows) {
        rows.collect { List<Map> row ->
            row.findAll { column -> column.value }.collectEntries { column ->
                Map columnInfo = columns[column['column-index'] as Integer] as Map
                [columnInfo.name, extractValue(column.value, columnInfo)]
            }.findAll { it.value }

        }
    }

    def extractValue(Object value, Map columnInfo) {
        try {

            switch (columnInfo.type) {
                case 'Text':
                    return value
                case 'Decimal':
                    return value as BigDecimal
                case 'Integer':
                    return value as Integer
                case 'Date':
                    long epochSecond = (value as Long).intdiv 1000


                    LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.ofHours(2))
                    ZoneId.systemDefault().rules
                    return localDateTime
                case 'Boolean':
                    return value == 'yes'
                default:
                    return value
            }
        } catch (ignore) {
            log.warn("Error parsing $columnInfo.id unknown for $value")
            return value
        }


    }

    private Map<Integer, Map> mapColumns(List<Map> maps) {
        maps.collectEntries {
            String header = it.header as String
            [it.index, [
                    name: normalizeName(header),
                    type: it.datatype
            ]]
        }
    }

    private String normalizeName(String header) {
        return header.uncapitalize().
                replaceAll(/[\s.]+/, '').
                replaceAll('æ','ae').
                replaceAll('Æ','Ae').
                replaceAll('ø','oe').
                replaceAll('Ø','Oe').
                replaceAll('å','aa').
                replaceAll('Å','Aa')

    }

}

import java.time.ZoneOffset
