package running.dinner.output

import com.craigburke.document.builder.WordDocumentBuilder
import running.dinner.data.Host
import running.dinner.data.Hosts

import static running.dinner.transfer.ExportImport.directory

class WordOutput {


    static void hostWineInformation(Hosts hosts) {

        WordDocumentBuilder wordBuilder = new WordDocumentBuilder(new File(directory, 'vin-information.docx'))
        greeting.delegate = wordBuilder
        Closure footer = footer()
        footer.delegate = wordBuilder
        wordBuilder.create {
            document(a4Args + [footer: footer]) {
                hosts.hosts.each { host ->
                    paragraph "Kære $host.shortNames", style: 'bold'
                    paragraph {
                        text "${host.hostAddress - ', Gl. Rye'}"
                        lineBreak()
                        text "Gl. Rye"
                    }
                    paragraph "Endnu engang tak fordi I vil være vært ved Running Dinner. Vi håber, det bliver en fest, der bliver talt om længe efter 7. marts."
                    paragraph "Her er vinen til forret og hovedret. Den er sponsoreret af Dagli'Brugsen - Byens Egen Butik.  Husk at det resterende indkøb til middagen med fordel kan gøres her i butikken. Det vil vi sætte stor pris på."
                    paragraph "Vedlagt er endnu en kuvert. Den må først åbnes den 7. marts, når forretten er spist!"
                    paragraph "Vi håber, I får en fantastisk aften!"
                    greeting.call()
                    pageBreak()
                    paragraph "Tidsplan for festen.", style: 'bold'
                    table(border: [size: 0], padding: 2.px, columns: [1.1, 9]) {
                        row {
                            cell "18:00", font : [family: 'Noto Mono']
                            cell "Første hold gæster kommer til forret."
                        }
                        row {
                            cell "19:30", font: [family: 'Noto Mono']
                            cell """\
                                Åbn den kuvert, som I modtog sammen med vinen fra Brugsen.
                                Giv hver gæst deres postkort, som fortæller, hvor de skal gå hen til hovedretten. 
                                Sig pænt farvel og gør klar til næste ryk ind.""".stripIndent()
                        }
                        row {
                            cell "20:00", font: [family: 'Noto Mono']
                            cell "Andet hold gæster kommer til hovedret."
                        }
                        row {
                            cell "21:30", font: [family: 'Noto Mono']
                            cell "Nu er det så småt tid til at afslutte hovedretten og bevæge sig op i hallen på Gl. Rye Skole, hvor festen fortsætter."
                        }
                        row {
                            cell "22:00", font: [family: 'Noto Mono']
                            cell """\
                                Servering af dessert lavet af Lyng Dal og kaffe, som er ristet og brygget Søhøjlandets Kaffe. 
                                Fest med bar og livemusik fra Dansefeber.""".stripIndent()
                        }
                        row {
                            cell "02:00", font: [family: 'Noto Mono']
                            cell "Festen slutter!"
                        }
                    }
                    paragraph 'Det er vigtigt, at tidspunkterne for forret og hovedret holdes.'
                    paragraph()
                    paragraph 'Husk at sende en varm tanke til vores sponsorer:'
                    paragraph {
                        text "- Dagli'Brugsen - Byens Egen Butik: Vin til maden"
                        lineBreak()
                        text("- Lyng Dal Hotel og Restaurant: Dessert")
                        lineBreak()
                        text("- Søhøjlandets Kaffe: Kaffe")
                    }
                    greeting.call()
                    pageBreak()

                }
            }
        }

    }

    static void hostEnvelopeWithPostcards(Hosts hosts) {

        WordDocumentBuilder wordBuilder = new WordDocumentBuilder(new File(directory, 'kuvertPostkort.docx'))
        greeting.delegate = wordBuilder
        Closure footer = footer()
        footer.delegate = wordBuilder
        wordBuilder.create {
            document(a5Args + [footer: footer]) {
                hosts.hosts.each { host ->
                    paragraph "Kære $host.shortNames", style: 'bold'
                    paragraph "Denne kuvert må først åbnes den 7. marts kl. 19:30. ", style: 'bold'
                    paragraph "Den indeholder et postkort til hver af jeres gæster, som fortæller dem, hvem de skal besøge til hovedretten."
                    paragraph "Vi håber I får en god aften sammen med jeres gæster!"
                    greeting.call()
                    pageBreak()
                }
            }
        }

    }

    static void guestsPostcards(Hosts hosts) {

        WordDocumentBuilder wordBuilder = new WordDocumentBuilder(new File(directory, 'postkort-gæster.docx'))
        greeting.delegate = wordBuilder
        Closure footer = footer(80)
        footer.delegate = wordBuilder
        wordBuilder.create {
            document(a6Args + [footer: footer]) {
                hosts.hosts.each { host ->
                    paragraph "Kære $host.shortNames", style: 'bold'
                    paragraph {
                        text "Endnu engang tusind tak fordi I er værter til dette års Running Dinner. "
                        text "Uden jer, ingen fest!", style: 'bold'
                    }
                    paragraph "I skal nu sige farvel til jeres forretsgæster og sende dem afsted med hvert sit postkort. "
                    paragraph "Det er vigtigt, at I får dem videre, så I kan være klar til jeres hovedretsgæster kl. 20:00. De er nemlig allerede på vej!"
                    paragraph "Fortsat god fest!"
                    greeting.call()
                    pageBreak()

                    host.entreCourseGuests.each { guestGroup ->
                        println "Postkort til ${guestGroup.names}"
                        Host nextHost = hosts.findMainCourseHost(guestGroup)
                        if (!nextHost) {
                            println "Missing host for ${guestGroup.shortNames}"
                        }
                        paragraph "(${host.shortNames})", style: 'silent', align: 'right'
                        paragraph "Kære $guestGroup.shortNames", style: 'bold'
                        paragraph {
                            text "Vi håber, at forretten smagte! Sig pænt tak for mad, tag overtøj på og gå hen til"
                            text " ${nextHost.shortNames}", style: 'bold'
                            text ", som vil servere hovedretten. Adressen er"
                            text " ${nextHost.hostAddress}.", style: 'bold'
                        }
                        paragraph "For at holde tidsplanen, skal I være der senest kl. 20. De andre gæster skal besøge nogle andre værter, men I mødes igen i hallen på Gl. Rye Skole kl. 22."
                        paragraph "Fortsat god fest!"
                        greeting.call()
                        pageBreak()
                    }

                }
            }
        }

    }


    private static a6Template = {
        'document'([margin: [top: 1.cm, left: 1.cm, right: 1.cm, bottom: 1.cm], font: [family: 'Montserrat Medium', color: '#000000', size: 9.pt]])
        'paragraph'([margin: [top: 0, bottom: 5]])
        'paragraph.bold'([font: [bold: true]])
        'text.bold'([font: [bold: true]])
        'paragraph.silent'(font: [size: 7.pt, color: '#999999'], margin: [top: 0, bottom: 0])
    }

    private static a6Args = [template: a6Template, size: 'a6', orientation: 'landscape']

    private static a5Template = {
        'document'([margin: [top: 1.cm, left: 1.cm, right: 1.cm, bottom: 1.cm], font: [family: 'Montserrat Medium', color: '#000000', size: 10.pt]])
        'paragraph'([margin: [top: 5, bottom: 5]])
        'paragraph.bold'([font: [bold: true]])
    }

    private static a5Args = [template: a5Template, size: 'a5', orientation: 'landscape']

    private static a4Template = {
        'document'([margin: [top: 2.cm, left: 2.cm, right: 2.cm, bottom: 2.cm], font: [family: 'Montserrat Medium', color: '#000000', size: 14.pt]])
        'paragraph'([margin: [top: 5, bottom: 5]])
        'paragraph.bold'([font: [bold: true]])
    }

    private static Map a4Args = [template: a4Template, size: 'a4']

    private static Closure greeting = { ->
        paragraph "De venligste hilsner fra"
        paragraph {
            text "Running Dinner udvalget i"
            lineBreak()
            text "Brugsens Bestyrelse"
        }

    }
//De venligste hilsner fra
//
//
//            Brugsens Bestyrelse.
    private static Closure footer(int width = 150) {
        return { info ->
            table(border: [size: 0], padding: 1.px) {
                row {
                    cell "Sponsorer", align: 'center'
                }
                row {
                    cell {
                        image(data: getImage('/daglibrugsen.png'), width: width, name: 'daglibrugsen.png')
                    }
                    cell(align: 'center') {
                        image(data: getImage('/lyngdal.png'), width: width, name: 'lyngdal.png')

                    }
                    cell(align: 'right') {
                        image(data: getImage('/søhøjlandets-kaffe.png'), width: width, name: 'søhøjlandets-kaffe.png')
                    }
                }
            }
        }
    }


    static byte[] getImage(String image) {
        WordOutput.getResourceAsStream(image).bytes
    }

}
