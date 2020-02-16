package running.dinner.templates

import running.dinner.data.Host

class MessageTemplates {
    static String createHostEmail(Host host) {
        String foodNotes = ''
        if(host.courses.entre.any { it.hensyn}) {
            foodNotes += """Til forretten er der bedt om flg. hensyn:
                ${host.courses.entre*.hensyn.findAll().collect { "* $it" }.join('\n                ')}
            """
        }
        if(host.courses.main.any { it.hensyn}) {
            foodNotes += """Til hovedretten er der bedt om flg. hensyn:
                ${host.courses.main*.hensyn.findAll().collect { "* $it"}.join('\n                ')}
            """
        }
        if(host.courses.entre.any { it.veganer }) {
            foodNotes += 'Til forretten er der en eller flere veganere.\n'
        }
        if(host.courses.main.any { it.veganer }) {
            foodNotes += 'Til hovedretten er der en eller flere veganere.\n'
        }
        return """\
            Kære ${host.names}
            
            Tak fordi I vil være vært til Running Dinner! I er sikkert spændte på, hvem der banker på jeres dør den 7. marts. Den spænding må I holde lidt endnu. Lige nu kan vi kun afsløre, at der kommer ${host.entres} til forret og ${host.mains} til hovedret (inkl. jer selv). I bestemmer selv, hvad I vil servere for jeres gæster${host.vegetar ? ' og vi har noteret at I serverer vegetarisk mad':''}. 

            ${foodNotes}
            Vi sørger for, at I får ${[host.entres, host.mains].max() * 125} kr. udbetalt ${host.mobilePay ? "på MobilePay (på mobil nr. ${host.mobilePay})" : "på bankkonto (*)"}, som bidrag til maden. I Byens Egen Butik kan I hente en pose med hvidvin og rødvin til maden sponsoreret af Brugsen.
            
            Her er en tidsplan for aftenen:
            
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
            
            Vi opfordrer til, at alle indkøb til festen foretages i Byens Egen Butik.
            
            Sponsorer:
            Dagli'Brugsen - Byens Egen Butik: Vin til maden
            Lyng Dal Hotel og Restaurant: Dessert
            Søhøjlandets Kaffe: Kaffe
            
            ${!host.mobilePay ? '*) Send registrerings- og kontonummer til info@runningdinner.nu - kan også bruges, hvis der er andre spørgsmål.' : 'Er der spørgsmål, kan de sendes på email til info@runningdinner.nu'}
            
            Velkommen til fest!
            
            På vegne af Brugsens Bestyrelse,
            
            Running Dinner udvalget.
            """.stripIndent()
    }

//    static String createGæstMail(Vært vært, Deltager deltager) {
//        """\
//            Kære ${deltager.navne}
//
//            Velkommen til Running Dinner.
//
//            Vi har arrangeret en fest, som vi håber, at alle vil tale om længe efter den 2. februar.
//
//            En del af aftenen er stadigvæk hemmelig, men på nuværende tidspunkt kan vi afsløre tidsplanen for aftenen:
//
//            18:00
//            Forret hos ${vært.navne} som bor ${vært.adresse}.
//            Værterne ved IKKE, hvem der kommer, så prøv at hold det hemmeligt.
//
//            19:30
//            Værten åbner en kuvert med et postkort til hver gæst, som fortæller, hvor I skal gå hen til hovedret.
//
//            20:00
//            Ankomst hos ny vært til hovedret.
//
//            22:00
//            Ankomst i Hall of Drums, Horsensvej 38. Servering af desert og kaffe. Fest med bar og DJ
//
//            03:00
//            Festen slutter.
//
//            Det er vigtigt, at tidspunkterne for forret og hovedret holdes.
//
//            Sponsorer:
//            Dagli'Brugsen - Byens Egen Butik: Vin til maden
//            Lyng Dal Hotel og Restaurant: Dessert
//            Søhøjlandets Kaffe: Kaffe
//            Hall of Drums: Lokale
//
//            Er der spørgsmål, kan de sendes på email til info@runningdinner.nu. Er det nødvendigt at komme i kontakt med værterne, sker det også på ovenstående email.
//
//            Velkommen til fest!
//
//            De bedste hilsner,
//            Dagli'Brugsen - Byens Egen Butik, Lyng Dal Hotel og Restaurant,
//            Søhøjlandets Kaffe og Hall of Drums
//        """.stripIndent()
//    }

//    static String createVærtPostkort(Vært vært) {
//        """\
//            Kære ${vært.navne}
//
//
//            Endnu engang tak fordi I vil være vært ved Running Dinner. Vi håber, det bliver en fest, der bliver talt om længe efter 2. februar.
//
//            Her er de fire flasker vin, som er sponsoreret af Dagli'Brugsen - Byens Egen Butik.  Husk at det resterende indkøb til middagen med fordel kan gøres her i butikken.
//
//            Vi håber, I får en fantastisk aften!
//
//            De bedste hilsner,
//            Dagli'Brugsen - Byens Egen Butik, Lyng Dal Hotel og Restaurant,
//            Søhøjlandets Kaffe og Hall of Drums
//        """.stripIndent()
//    }

//    static String createVærtKuvert(Vært vært) {
//        """\
//            Til ${vært.navne}
//            ${vært.adresse}
//
//            Denne kuvert må først åbnes den 2. februar kl. 19:30. Den indeholder et postkort til hver af jeres gæster, som fortæller dem, hvem de skal besøge til hovedretten.
//
//            Det er vigtigt, at I får dem videre, så I kan være klar til jeres hovedretsgæster kl. 20:00. De er nemlig allerede på vej!
//
//            Fortsat god fest!
//
//            De bedste hilsner,
//            Dagli'Brugsen - Byens Egen Butik, Lyng Dal Hotel og Restaurant,
//            Søhøjlandets Kaffe og Hall of Drums
//        """.stripIndent()
//
//    }

//    static String createGæstPostkort(Vært vært, Deltager deltager) {
//        """\
//            Kære ${deltager.navne}
//
//
//            Vi håber, at forretten smagte! Sig pænt tak for mad, tag overtøj på og gå hen til ${vært.navne}, som vil servere hovedretten.
//
//            Adressen er ${vært.adresse}, og husk at være der senest kl. 20:00. De andre gæster skal besøge nogle andre værter, men I mødes igen i Hall of Drums kl. 22.
//
//            Fortsat god aften!
//
//            De bedste hilsner,
//            Byens Egen Butik, Lyng Dal Hotel og Restaurant,
//            Søhøjlandets Kaffe og Hall of Drums
//
//
//
//        """.stripIndent()
//    }
}
