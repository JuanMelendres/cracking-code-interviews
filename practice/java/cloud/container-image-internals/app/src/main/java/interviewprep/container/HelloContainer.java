package interviewprep.container;

import com.google.gson.Gson;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HelloContainer {

    public static void main(String[] args) {
        Map<String, Object> facts = new LinkedHashMap<>();
        facts.put("pid", ProcessHandle.current().pid());
        facts.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        facts.put("maxHeapBytes", Runtime.getRuntime().maxMemory());
        facts.put("jvmName", ManagementFactory.getRuntimeMXBean().getVmName());
        facts.put("demoBuildMarker", "v2");
        facts.put("jvmVersion", System.getProperty("java.version"));

        Gson gson = new Gson();
        System.out.println(gson.toJson(facts));
    }
}
