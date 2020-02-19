package running.dinner.templates

import running.dinner.data.Guest
import running.dinner.data.GuestGroup
import running.dinner.data.Host

class MessageTemplates {
    static String createHostEmail(Host host) {
        String foodNotes = ''
        foodNotes += hensyn(host.entreCourseGuests, 'forretten')
        foodNotes += hensyn(host.mainCourseGuests, 'hovedretten')
        if (host.entreCourseGuests*.guests.flatten().any { it.veganer }) {
            foodNotes += 'Til forretten er der en eller flere veganere.\n'
        }
        if (host.mainCourseGuests*.guests.flatten().any { it.veganer }) {
            foodNotes += 'Til hovedretten er der en eller flere veganere.\n'
        }
        return """\
            Kære ${host.shortNames}
            
            Tak fordi I vil være vært til Running Dinner! I er sikkert spændte på, hvem der banker på jeres dør den 7. marts. Den spænding må I holde lidt endnu. Lige nu kan vi kun afsløre, at der kommer ${host.entreCourseSeats} til forret og ${host.mainCourseSeats} til hovedret (inkl. jer selv). I bestemmer selv, hvad I vil servere for jeres gæster${host.vegetar ? ' og vi har noteret at I serverer vegetarisk mad' : ''}. 

            ${foodNotes}
            Vi sørger for, at I får ${[host.entreCourseSeats, host.mainCourseSeats].max() * 125} kr. udbetalt ${host.mobilePay ? "på MobilePay (på mobil nr. ${host.mobilePay})" : "på bankkonto (*)"}, som bidrag til maden. Vi opfordrer til, at alle indkøb til festen foretages i Byens Egen Butik.
            
            Fra den 1. marts kan der i Byens Egen Butik hentes hvidvin og rødvin til maden, udvangt af Henrik og  sponsoreret af Brugsen. Her får I også udleveret en kuvert som først skal åbnes efter forretten den 7. marts.
            
            Her er en tidsplan for den 7. marts:
            
                18:00 
                Første hold gæster kommer til forret.
                
                19:30 
                Åbn den kuvert, som I modtager sammen med vinen fra Brugsen. 
                Giv hver gæst deres postkort, som fortæller, hvor de skal gå hen til hovedretten. 
                Sig pænt farvel og gør klar til næste ryk ind.
                
                20:00 
                Andet hold gæster kommer til hovedret.
                
                22:00 
                Ankomst i hallen på Gl. Rye Skole. Servering af desert og kaffe. Fest med bar og livemusik.
                
                02:00 
                Festen slutter. 
            
            Det er vigtigt, at tidspunkterne for forret og hovedret holdes.
            
            
            Sponsorer:
            Dagli'Brugsen - Byens Egen Butik: Vin til maden
            Lyng Dal Hotel og Restaurant: Dessert
            Søhøjlandets Kaffe: Kaffe
            
            ${!host.mobilePay ? '*) Send registrerings- og kontonummer til info@runningdinner.nu - kan også bruges, hvis der er andre spørgsmål.' : 'Er der spørgsmål, kan de sendes på email til info@runningdinner.nu'}
            
            Velkommen til fest!
            
            De venligste hilsner fra
            
            Brugsens Bestyrelses Running Dinner udvalg.
        """.stripIndent()
    }

    private static String hensyn(List<GuestGroup> guestGroups, String ret) {
        List<Guest> guests = guestGroups.guests.flatten() as List<Guest>

        if (guests.any { it.hensyn }) {
            return """Til ${ret} er der bedt om flg. hensyn:
                ${guests.findAll { it.hensyn }.collect { "* $it.hensyn" }.join('\n                ')}
            """
        }
        return ''
    }


    static String createGuestMail(Host host, GuestGroup guestGroup) {
        """\
            Kære ${guestGroup.shortNames}

            Velkommen til Running Dinner.

            Vi har arrangeret en fest, som vi håber, at alle vil tale om længe efter den 7. marts.

            En del af aftenen er stadigvæk hemmelig, men på nuværende tidspunkt kan vi afsløre tidsplanen for aftenen:

                18:00
                Forret hos ${host.shortNames} som bor ${host.hostAddress}.
                Værterne ved IKKE, hvem der kommer, så prøv at hold det hemmeligt.
    
                19:30
                Værten åbner en kuvert med et postkort til hver gæst, som fortæller, hvor I skal gå hen til hovedret.
    
                20:00
                Ankomst hos ny vært til hovedret.
    
                22:00
                Ankomst i hallen på Gl. Rye Skole. Servering af desert og kaffe. Fest med bar og livemusik
    
                02:00
                Festen slutter.

            Det er vigtigt, at tidspunkterne for forret og hovedret holdes.

            Sponsorer:
            Dagli'Brugsen - Byens Egen Butik: Vin til maden
            Lyng Dal Hotel og Restaurant: Dessert
            Søhøjlandets Kaffe: Kaffe

            Er der spørgsmål, kan de sendes på email til info@runningdinner.nu. Er det nødvendigt at komme i kontakt med værterne, sker det også på ovenstående email.

            Velkommen til fest!

            De venligste hilsner fra
            
            Brugsens Bestyrelses Running Dinner udvalg.
        """.stripIndent()
    }
}
