package running.dinner.output

import com.craigburke.document.builder.WordDocumentBuilder
import running.dinner.data.Host

class WordOutput {


    static void hostWineInformation(List<Host> hosts) {

        WordDocumentBuilder wordBuilder = new WordDocumentBuilder(new File('vin-information.docx'))
        greeting.delegate = wordBuilder
        footer.delegate = wordBuilder
        wordBuilder.create {
            document(a4Args + [footer: footer]) {
                hosts.each { host ->
                    paragraph "Kære $host.shortNames", style: 'bold'
                    paragraph "Endnu engang tak fordi I vil være vært ved Running Dinner. Vi håber, det bliver en fest, der bliver talt om længe efter 7. marts."
                    paragraph "Her er vinen til forret og hovedret. Den er sponsoreret af Dagli'Brugsen - Byens Egen Butik.  Husk at det resterende indkøb til middagen med fordel kan gøres her i butikken."
                    paragraph "Vedlagt er endnu en kuvert. Den må først åbnes den 7. marts når forretten er spist!"
                    paragraph "Vi håber, I får en fantastisk aften!"
                    greeting.call()
                    pageBreak()
                }
            }
        }

    }
    static void hostEnvelopeWithPostcards(List<Host> hosts) {

        WordDocumentBuilder wordBuilder = new WordDocumentBuilder(new File('kuvertPostkort.docx'))
        greeting.delegate = wordBuilder
        footer.delegate = wordBuilder
        wordBuilder.create {
            document(a5Args + [footer: footer]) {
                hosts.each { host ->
                    paragraph "Kære $host.shortNames", style: 'bold'
                    paragraph "${host.hostAddress}"
                    paragraph "Denne kuvert må først åbnes den 7. marts kl. 19:30. Den indeholder et postkort til hver af jeres gæster, som fortæller dem, hvem de skal besøge til hovedretten."
                    paragraph "Det er vigtigt, at I får dem videre, så I kan være klar til jeres hovedretsgæster kl. 20:00. De er nemlig allerede på vej!"
                    paragraph "Fortsat god fest!"
                    greeting.call()
                    pageBreak()
                }
            }
        }

    }

   static void guestsPostcards(List<Host> hosts) {

        WordDocumentBuilder wordBuilder = new WordDocumentBuilder(new File('gæste-postkort.docx'))
        greeting.delegate = wordBuilder
        footer.delegate = wordBuilder
        wordBuilder.create {
            document(a5Args + [footer: footer]) {
                hosts.each { host ->
                    paragraph "Kære $host.shortNames", style: 'bold'
                    paragraph "${host.hostAddress}"
                    paragraph "Denne kuvert må først åbnes den 7. marts kl. 19:30. Den indeholder et postkort til hver af jeres gæster, som fortæller dem, hvem de skal besøge til hovedretten."
                    paragraph "Det er vigtigt, at I får dem videre, så I kan være klar til jeres hovedretsgæster kl. 20:00. De er nemlig allerede på vej!"
                    paragraph "Fortsat god fest!"
                    greeting.call()
                    pageBreak()
                }
            }
        }

    }


    private static a6Template = {
        'document'([margin: [top: 1.cm, left: 1.cm, right: 1.cm, bottom: 1.cm], font: [family: 'Helvetica', color: '#000000', size: 10.pt]])
        'paragraph'([margin: [top: 5, bottom: 5]])
        'paragraph.bold'([font: [bold: true]])
    }

    private static a6Args = [template: a6Template, size: 'a6', orientation: 'landscape']

    private static a5Template = {
        'document'([margin: [top: 1.cm, left: 1.cm, right: 1.cm, bottom: 1.cm], font: [family: 'Helvetica', color: '#000000', size: 10.pt]])
        'paragraph'([margin: [top: 5, bottom: 5]])
        'paragraph.bold'([font: [bold: true]])
    }

    private static a5Args = [template: a5Template, size: 'a5', orientation: 'landscape']

    private static a4Template = {
        'document'([margin: [top: 2.cm, left: 2.cm, right: 2.cm, bottom: 2.cm], font: [family: 'Helvetica', color: '#000000', size: 14.pt]])
        'paragraph'([margin: [top: 5, bottom: 5]])
        'paragraph.bold'([font: [bold: true]])
    }

    private static Map a4Args = [template: a4Template, size: 'a4', orientation: 'landscape',]

    private static Closure greeting = { ->
        paragraph "Med venlig hilsen"
        paragraph "Brugsens Bestyrelse"

    }
    private static Closure footer = { info ->
        table(border: [size: 0]) {
            row {
                cell "Sponsorer", align: 'center'
            }
            row {
                cell {
                    image(data: getImage('/daglibrugsen.png'), width: 150.px, name: 'daglibrugsen.png')
                }
                cell(align: 'center') {
                    image(data: getImage('/lyngdal.png'), width: 150.px, name: 'lyngdal.png')

                }
                cell(align: 'right') {
                    image(data: getImage('/søhøjlandets-kaffe.png'), width: 150.px, name: 'søhøjlandets-kaffe.png')
                }
            }
        }
    }

    static byte[] getImage(String image) {
        WordOutput.getResourceAsStream(image).bytes
    }
}
