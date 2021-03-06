package running.dinner.output

import builders.dsl.spreadsheet.builder.poi.PoiSpreadsheetBuilder
import running.dinner.data.GuestGroup
import running.dinner.data.Host
import running.dinner.data.Hosts

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
            sheet('Værter') {
                freeze 1, 1
                row {
                    cell {
                        width 5 cm
                        value 'Værter'
                        style { font { style bold } }
                    }
                    cell {
                        width 3 cm
                        value 'Adresse'
                        style { font { style bold } }
                    }
                    cell {
                        width 2 cm
                        value 'Telefon'
                        style { font { style bold } }
                    }
                    cell {
                        width 1 cm
                        value 'Forret'
                    }
                    cell {
                        width 1 cm
                        value 'Hovedret'
                    }
                    cell {
                        width 1 cm
                        value 'Max gæster'
                    }
                    cell {
                        width 1 cm
                        value 'Hvidvin og Rødvin'
                    }
                    cell {
                        width 1 cm
                        value 'Udbetaling'
                    }
                    cell {
                        width 1 cm
                        value "Mobile Pay nr"
                    }
                }
                hosts.hosts.sort { it.shortNames }.each { host ->
                    row {
                        cell host.shortNames
                        cell host.hostAddress
                        cell "${host.guests*.mobile.join(', ')}"
                        cell host.entreCourseSeats
                        cell host.mainCourseSeats
                        cell host.maxGuests
                        cell host.maxGuests <= 8 ? 2 : 3
                        cell([host.entreCourseSeats, host.mainCourseSeats].max() * 125)
                        cell host.mobilePay ?: 'konto'
                    }
                }
                row {
                    cell()
                    cell()
                    cell()
                    cell(hosts.hosts.sum { it.entreCourseSeats })
                    cell(hosts.hosts.sum { it.mainCourseSeats })
                    cell(hosts.hosts.sum { it.maxGuests })
                    cell(hosts.hosts.sum { it.maxGuests <= 8 ? 2 : 3 })
                    cell(hosts.hosts.sum { [it.entreCourseSeats, it.mainCourseSeats].max() * 125 })
                }


            }

            hosts.hosts.sort { (it.vegetar ? -1 : 0) ?: it.shortNames }.each { host ->
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
                                        background "${index % 2 ? '#dddddd' : '#eeeeee'}"
                                    }
                                }
                                cell "${[(guest.single ? 'Single' : null), (guest.allergy ? guest.hensyn : guest.veganer ? 'Veganer' : guest.vegetar ? 'Vegetar' : null)].findAll().join(', ')}"
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
                                cell "${[(guest.single ? 'Single' : null), (guest.allergy ? guest.hensyn : guest.veganer ? 'Veganer' : guest.vegetar ? 'Vegetar' : null)].findAll().join(', ')}"
                            }
                        }
                    }
                }

            }
        }
    }

    static buildAddresses(Hosts hosts) {
        File file = new File(directory, 'host-adresser.xlsx')
        println file.absolutePath

        PoiSpreadsheetBuilder.create(file).build {
            sheet('Adresser') {
                freeze(0, 1)
                row {
                    cell {
                        value 'Værter'
                    }
                    cell {
                        value 'Navn'
                    }
                    cell {
                        value 'Adresse'
                    }
                }
                hosts.hosts.sort { it.shortNames }.each { host ->
                    row {
                        cell {
                            value 'Værter'
                        }
                        cell host.shortNames
                        cell "${host.hostAddress - ', Gl. Rye' + ', Gammel Rye, 8680 Ry'}"
                    }

                }
            }
        }
    }
}
