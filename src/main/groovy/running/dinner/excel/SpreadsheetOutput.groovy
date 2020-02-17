package running.dinner.excel


import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import running.dinner.data.Host

class SpreadsheetOutput {
    static buildSpreadsheet(List<Host> hosts) {
        File file = new File('spreadsheet.xlsx')
        println file.absolutePath

        PoiSpreadsheetBuilder.create(file).build {
            hosts.sort { it.vegetar }.each { host ->
                sheet("$host.shortNames ${host.vegetar ? '(Vegetar)' : ''}") {
                    row {
                        cell 'Værter'
                        cell {
                            width 8 cm
                            value host.shortNames
                            style {
                                font { style bold }
                            }
                        }
                    }
                    row {
                        cell 'Adresse'
                        cell host.hostAddress
                    }
                    row {
                        cell 'Max gæster'
                        cell host.maxGuests
                        cell host.vegetar ? 'Vegetar' : ''
                        cell host.allergenes ? 'Kan håndtere særlige behov' : ''
                    }
                    row {
                        cell 'Forret'
                        cell host.entres
                    }
                    host.courses.entre.each { guest ->
                        row {
                            cell()
                            cell "${guest.name}"
                            cell "${guest.allergy ? guest.hensyn ?: '' : guest.veganer ? 'Veganer' : guest.vegetar ? 'Vegetar' : ''}"
                        }
                    }
                    row {
                        cell 'Hovedret'
                        cell host.mains
                    }
                    host.courses.main.each { guest ->
                        row {
                            cell()
                            cell "${guest.name}"
                            cell "${guest.allergy ? guest.hensyn : guest.veganer ? 'Veganer' : guest.vegetar ? 'Vegetar' : ''}"
                        }
                    }
                }

            }
        }
    }
}
