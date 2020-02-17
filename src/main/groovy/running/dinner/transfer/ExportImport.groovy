package running.dinner.transfer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import running.dinner.data.Host
import running.dinner.data.Hosts

class ExportImport {
    static ObjectMapper mapper = new ObjectMapper()

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
    }

    static void exportData(List<Host> hosts) {
        File file = new File('hosts.json')

        mapper.writeValue(file, new Hosts(hosts: hosts))
    }

    static List<Host> importData() {
        File file = new File('hosts.json')

        if(file.exists()) {
            Hosts hosts = mapper.readValue(file, Hosts)

            return hosts.hosts
        }
        return null

    }
}
