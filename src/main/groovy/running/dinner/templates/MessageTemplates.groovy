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
            
            Fra den 1. marts kan der i Byens Egen Butik hentes hvidvin og rødvin til maden, udvalgt af Henrik og  sponsoreret af Brugsen. Her får I også udleveret en kuvert som først skal åbnes efter forretten.
            
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
                Ankomst i hallen på Gl. Rye Skole. Servering af desert og kaffe. Fest med bar og livemusik fra Dansefeber.
                
                02:00 
                Festen slutter. 
            
            Det er vigtigt, at tidspunkterne for forret og hovedret holdes.
                       
            ${!host.mobilePay ? '*) Send registrerings- og kontonummer til info@runningdinner.nu - kan også bruges, hvis der er andre spørgsmål.' : 'Er der spørgsmål, kan de sendes på email til info@runningdinner.nu'}
            
            Husk at sende en varm tanke til vores sponsorer:
            - Dagli'Brugsen - Byens Egen Butik: Vin til maden
            - Lyng Dal Hotel og Restaurant: Dessert
            - Søhøjlandets Kaffe: Kaffe

            Også en stor tak til:
            - Skolebestyrelsen på Gl. Rye Skole: For at stille hallen til rådighed.
            - Gl. Ryes Borgerforening: For Mobile Pay til festen.
            - Initiativgruppen af 99: For hjælp til pyntning af hallen.

            Velkommen til fest!
            
        """.stripIndent() + greeting
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
                Ankomst i hallen på Gl. Rye Skole. Servering af desert og kaffe. Fest med bar og livemusik fra Dansefeber
    
                02:00
                Festen slutter.

            Det er vigtigt, at tidspunkterne for forret og hovedret holdes.

            Er der spørgsmål, kan de sendes på email til info@runningdinner.nu. Er det nødvendigt at komme i kontakt med værterne, sker det også på ovenstående email.

            Husk at sende en varm tanke til vores sponsorer:
            - Dagli'Brugsen - Byens Egen Butik: Vin til maden
            - Lyng Dal Hotel og Restaurant: Dessert
            - Søhøjlandets Kaffe: Kaffe

            Også en stor tak til:
            - Skolebestyrelsen på Gl. Rye Skole: For at stille hallen til rådighed.
            - Gl. Ryes Borgerforening: For Mobile Pay til festen.
            - Initiativgruppen af 99: For hjælp til pyntning af hallen.

            Velkommen til fest!

        """.stripIndent() + greeting
    }

    static String createTeaserMail(GuestGroup guestGroup) {
        """\
            Kære ${guestGroup.shortNames}

            Nu skal vi snart feste sammen! Der er 299 tilmeldte og værter nok til alle. WOW!

            Musikken er bestilt. Det bliver Dansefeber, der spiller op! 

            Lige nu har vi travlt med at få de sidste detaljer på plads. I den kommende uge vil ${guestGroup.size == 1 ? 'du' : 'I'} modtage mere information på email.

            Lige nu skal I bare glæde jer!
            
            Er der spørgsmål, kan de sendes på email til info@runningdinner.nu.

            Husk at sende en varm tanke til vores sponsorer:
            - Dagli'Brugsen - Byens Egen Butik: Vin til maden
            - Lyng Dal Hotel og Restaurant: Dessert
            - Søhøjlandets Kaffe: Kaffe

            Også en stor tak til:
            - Skolebestyrelsen på Gl. Rye Skole: For at stille hallen til rådighed.
            - Gl. Ryes Borgerforening: For Mobile Pay til festen.
            - Initiativgruppen af 99: For hjælp til pyntning af hallen.

        """.stripIndent() + greeting
    }

    static String createCorrection(GuestGroup guestGroup) {
        """\
            Kære ${guestGroup.shortNames}

            Der er desværre indsneget sig en fejl på det postkort I vil modtage, når I er til forret. 

            På kortet vil der stå husnummeret 10, men det rigtige husnummer er 15. Så I skal altså gå til adressen på kortet, men udskifte nr. 10 med nr. 15.

            I ikke får hele adressen, for så forsvinder overraskelsen  :-)

            Vi prøver også at sende en reminder SMS ud til jer på lørdag omkring kl. 19:30.

        """.stripIndent() + greeting
    }

    static String createHelperMail(Guest guest) {
        """\
            Kære ${guest.name}

            Nu er der kun en uge til, vi skal holde fest. Alle detaljer er ved at være på plads, og nu kommer alt det praktisk arbejde med at få selve festen stablet på benene.

            Du skrev i din tilmelding, at du gerne vil hjælpe, og det er mægtig glade for! Vi har nemlig brug for en masse hænder til at gøre hallen klar til fest, og til at gøre den klar til skolebørnene igen inden mandag morgen. Vi kan kun klare det ved fælles hjælp.

            Vi har brug for hjælp til opsætning og oppyntning i hallen lørdag fra ca. 10:00 - 16:00, og oprydning og rengøring i hallen søndag fra 9:30 - 14:00. Hvis vi er mange, kan vi sikkert gøre det hele på kortere tid. 

            Hvis du ikke har mulighed for at hjælpe lørdag eller søndag har vi også andre opgaver før festen. Bl.a. har vi ting, der skal hentes rundt omkring i løbet af ugen og pynt, der skal forberedes. 

            For at få overblik over, hvor mange hænder, vi har til rådighed, vil vi bede dig udfylde et ganske kort spørgeskema (det tager under et minut), så vi ved, hvornår du har tid:

            https://docs.google.com/forms/d/e/1FAIpQLSc7S9hOk5wyjFMiXuenks3vfpg9-RaQ73pYbv-qgH4ykh3vZA/viewform?usp=pp_url&entry.541231881=${URLEncoder.encode(guest.name, 'UTF-8')}&entry.1206991931=${guest.email}&entry.488223695=${guest.mobile}

            På forhånd tak,            

        """.stripIndent() + greeting
    }

    private static String getGreeting() {
        """\
            De venligste hilsner fra
            
            Running Dinner udvalget i
            Brugsens Bestyrelse.
        """.stripIndent()
    }
}
