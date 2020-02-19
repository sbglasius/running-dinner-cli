package running.dinner.output


import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import running.dinner.data.GuestGroup
import running.dinner.data.Host
import running.dinner.data.Hosts
import running.dinner.transfer.ExportImport

import static running.dinner.transfer.ExportImport.directory

class SpreadsheetOutput {
    static buildSpreadsheet(Hosts hosts) {
        File file = new File(directory, 'spreadsheet.xlsx')
        println file.absolutePath

        PoiSpreadsheetBuilder.create(file).build {
            sheet('Gæster') {
                freeze 1, 1
                row {
                    cell {
                        width 5 cm
                        value 'Gæster'
                        style { font { style bold } }
                    }
                    cell {
                        width 6 cm
                        value 'Email'
                        style { font { style bold } }
                    }
                    cell {
                        width 2.5 cm
                        value 'Telefon'
                        style { font { style bold } }
                    }
                    cell {
                        width 5 cm
                        value 'Forret navn'
                        style { font { style bold } }
                    }
                    cell {
                        width 3 cm
                        value 'Forret Adresse'
                        style { font { style bold } }
                    }
                    cell {
                        width 6 cm
                        value 'Forret kontakt'
                        style { font { style bold } }
                    }
                    cell {
                        width 5 cm
                        value 'Hovedret navn'
                        style { font { style bold } }
                    }
                    cell {
                        width 3 cm
                        value 'Hovedret Adresse'
                        style { font { style bold } }
                    }
                    cell {
                        width 6 cm
                        value 'Hovedret kontakt'
                        style { font { style bold } }
                    }
                }
                hosts.allGuests.each { GuestGroup guestGroup ->
                    Host entreHost = hosts.findEntreCourseHost(guestGroup)
                    Host mainHost = hosts.findMainCourseHost(guestGroup)
                    row {
                        cell "${guestGroup.shortNames}"
                        cell "${guestGroup.guests*.email.join(', ')}"
                        cell "${guestGroup.guests*.mobile.join(', ')}"
                        cell "${entreHost.shortNames}"
                        cell "${entreHost.hostAddress}"
                        cell "${entreHost.guests*.email.join(', ')}, ${entreHost.guests*.mobile.join(', ')}"
                        cell "${mainHost.shortNames}"
                        cell "${mainHost.hostAddress}"
                        cell "${mainHost.guests*.email.join(', ')}, ${mainHost.guests*.mobile.join(', ')}"
                    }
                }
            }
            hosts.hosts.sort { it.vegetar }.each { host ->
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
                        cell 'Mobil'
                        cell host.guests*.mobile.join(', ')
                    }
                    row {
                        cell 'Email'
                        cell host.guests*.email.join(', ')
                    }
                    row()
                    row {
                        cell 'Max antal gæster'
                        cell {
                            style {
                                align top, left
                            }
                            value host.maxGuests
                        }
                        cell host.vegetar ? 'Vegetar' : ''
                        cell host.allergenes ? 'Kan håndtere særlige behov' : ''
                    }
                    row {
                        cell 'Forret gæster'
                        cell {
                            style {
                                align top, left
                            }
                            value host.entreCourseSeats
                        }
                    }
                    host.entreCourseGuests.eachWithIndex { guestGroup, index ->
                        guestGroup.guests.each { guest ->
                            row {
                                cell()
                                cell {
                                    value "${guest.name}"
                                    style {
                                        background "${index % 2 ? '#dddddd':'#eeeeee'}"
                                    }
                                }
                                cell "${guest.allergy ? guest.hensyn ?: '' : guest.veganer ? 'Veganer' : guest.vegetar ? 'Vegetar' : ''}"
                            }
                        }
                    }
                    row {
                        cell 'Hovedret gæster'
                        cell {
                            style {
                                align top, left
                            }
                            value host.mainCourseSeats
                        }
                    }
                    host.mainCourseGuests.eachWithIndex { guestGroup, index ->
                        guestGroup.guests.each { guest ->
                            row {
                                cell()
                                cell {
                                    value "${guest.name}"
                                    style {
                                        background "${index % 2 ? '#dddddd' : '#eeeeee'}"
                                    }
                                }
                                cell "${guest.allergy ? guest.hensyn : guest.veganer ? 'Veganer' : guest.vegetar ? 'Vegetar' : ''}"
                            }
                        }
                    }
                }

            }
        }
    }

}
