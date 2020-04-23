import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.neotys.prometheus.datamodel.PrometheusIndicators;
import com.neotys.prometheus.datamodel.PrometheusResponse;
import org.junit.Test;

import static com.neotys.prometheus.common.Constants.*;

public class PrometheusTest {
    String Json="{\"status\":\"success\",\"data\":{\"resultType\":\"matrix\",\"result\":[{\"metric\":{\"__name__\":\"go_gc_duration_seconds\",\"endpoint\":\"http-metrics\",\"instance\":\"10.1.0.255:10255\",\"job\":\"kubelet\",\"metrics_path\":\"/metrics\",\"namespace\":\"kube-system\",\"node\":\"aks-nodepool1-23661029-vmss000001\",\"quantile\":\"0.25\",\"service\":\"prometheus-operator-kubelet\"},\"values\":[[1587656014,\"0.000012001\"],[1587656019,\"0.000012001\"],[1587656024,\"0.000012001\"]]}]}}";


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
