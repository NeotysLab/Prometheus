import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.neotys.prometheus.datamodel.PrometheusIndicators;
import com.neotys.prometheus.datamodel.PrometheusResponse;
import org.junit.Test;

import static com.neotys.prometheus.common.Constants.*;

public class PrometheusTest {
    String Json="{\n" +
            "    \"status\": \"success\",\n" +
            "    \"data\": {\n" +
            "        \"resultType\": \"matrix\",\n" +
            "        \"result\": [\n" +
            "            {\n" +
            "                \"metric\": {\n" +
            "                    \"__name__\": \"go_gc_duration_seconds\",\n" +
            "                    \"instance\": \"localhost:9090\",\n" +
            "                    \"job\": \"prometheus\",\n" +
            "                    \"quantile\": \"0.25\"\n" +
            "                },\n" +
            "                \"values\": [\n" +
            "                    [\n" +
            "                        1587449541,\n" +
            "                        \"0.00001111111\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449546,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449551,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449556,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449561,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449566,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449571,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449576,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449581,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449586,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449591,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449596,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449601,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449606,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449611,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449616,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449621,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449626,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449631,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449636,\n" +
            "                        \"0.000027665\"\n" +
            "                    ],\n" +
            "                    [\n" +
            "                        1587449641,\n" +
            "                        \"0.000027665\"\n" +
            "                    ]\n" +
            "                ]\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";


    @Test
    public void testSerialization()
    {
        PrometheusResponse prometheusResponse= new GsonBuilder().create().fromJson(Json, PrometheusResponse.class);
        System.out.println(prometheusResponse);
    }
    @Test
    public void testserializationindicators()
    {
        String contenet="{ \n" +
                "  indicatorList : [\n" +
                "\t\t{\n" +
                "\t\t\tmetricName :\"go_gc_duration_seconds\",\n" +
                "\t\t\tlabels : \n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t  job: \"prometheus\",\n" +
                "\t\t\t\tquantile : \"0.25\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\tunit:\"s\"\n" +
                "\t\t}\n" +
                "\t\n" +
                "\t]\n" +
                "\n" +
                "}";
        PrometheusIndicators prometheusIndicators= new GsonBuilder().create().fromJson(contenet,PrometheusIndicators.class);
        System.out.println(prometheusIndicators.toString());
    }
    @Test
    public void testString() {

        String s = "quantile";
        String s2 = "!~0.25";
        if (s2.startsWith(NOREGEXP))
            System.out.println(s + NOREGEXP + "\"" + s2.substring(2, s2.length()) + "\",");
        else {
            if (s2.startsWith(REGEXP))
                System.out.println(s + REGEXP + "\"" + s2.substring(2, s2.length()) + "\",");
            else {
                if (s2.startsWith(NOTEQUAL))
                    System.out.println(s + NOTEQUAL + "\"" + s2.substring(2, s2.length()) + "\",");
                else
                    System.out.println(s + EQUAL + "\"" + s2.substring(1, s2.length()) + "\",");
            }
        }
    }

}
