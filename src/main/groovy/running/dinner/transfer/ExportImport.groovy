package running.dinner.transfer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import running.dinner.data.Hosts

class ExportImport {
    static ObjectMapper mapper = new ObjectMapper()
    static File directory = new File('./output')

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        directory.mkdirs()
    }

    static void exportData(Hosts hosts) {
        File file = new File(directory, 'hosts.json')

        mapper.writeValue(file, hosts)
    }

    static Hosts importData() {
        File file = new File(directory, 'hosts.json')

        if(file.exists()) {
            Hosts hosts = mapper.readValue(file, Hosts)

            return hosts
        }
        return null

    }
}
