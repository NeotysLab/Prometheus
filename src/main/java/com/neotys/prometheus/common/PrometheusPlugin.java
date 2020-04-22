package com.neotys.prometheus.common;

import com.google.common.base.Optional;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Proxy;
import com.neotys.prometheus.datamodel.PrometheusIndicator;
import com.neotys.prometheus.datamodel.PrometheusIndicators;
import com.neotys.prometheus.datamodel.PrometheusResponse;
import com.neotys.rest.dataexchange.model.Entry;
import com.neotys.rest.dataexchange.util.Entries;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neotys.prometheus.common.Constants.*;
import static com.neotys.prometheus.common.HTTPGenerator.HTTP_GET_METHOD;
import static com.neotys.prometheus.common.HttpResponseUtils.getJsonResponse;

public class PrometheusPlugin {
    private static PrometheusPlugin instance;
    private static final String NLWEB_VERSION = "v1";
    private PrometheusIndicators prometheusIndicators;
    private Long starttime;
    private String vitualUserID;
    private boolean tracemode;
    private Context context;
    private Long endtime;
    private  String dataExchangeApiUrl;
    private Optional<String> proxyName;
    private String prometheusHost;
    private String prometheusPort;

    private PrometheusPlugin(String host,String port,PrometheusIndicators prometheusIndicators, Long starttime, String vitualUserID, Context context,final String dataExchangeApiUrl,
                             final Optional<String> proxyName,boolean tracemode) {
        this.prometheusHost=host;
        this.prometheusPort=port;
        this.prometheusIndicators = prometheusIndicators;
        if(starttime==null)
        {
            starttime= System.currentTimeMillis()/1000;
            starttime-=20;
        }
        this.starttime = starttime;
        this.endtime=starttime+PROMETHEUS_MONITORING_RANGE;
        this.vitualUserID = vitualUserID;
        this.context=context;
        this.tracemode=tracemode;
        this.dataExchangeApiUrl=dataExchangeApiUrl;
        this.proxyName=proxyName;
    }

    public synchronized static PrometheusPlugin getInstance(String host,String port,PrometheusIndicators prometheusIndicators, Long starttime, String vitualUserID, Context context,final String dataExchangeApiUrl, final Optional<String> proxyName,boolean tracemode)  throws Exception {
        if (instance == null) {
            instance = new PrometheusPlugin(host,port,
                    prometheusIndicators,
                    starttime,
                    vitualUserID,
                    context,
                    dataExchangeApiUrl,
                    proxyName,
                    tracemode);
        }else{
            instance.setContext(context);
        }
        return instance;
    }
    public static Optional<Proxy> getProxy(final Context context, final Optional<String> proxyName, final String url) throws MalformedURLException {
        if (proxyName.isPresent()) {
            return Optional.fromNullable(context.getProxyByName(proxyName.get(), new URL(url)));
        }
        return Optional.absent();
    }
    private List<Entry> getINdicatorsData(PrometheusIndicator indicator) throws Exception {
        final String url=PROMETHEUS_API_PROTOCOL+prometheusHost+":"+prometheusPort+PROMETHEUS_QUERY_PATH;
        final Optional<Proxy> proxy = getProxy(context, proxyName, url);
        final HashMap<String,String> header=new HashMap<>();
        List<Entry> result=new ArrayList<>();
        final HTTPGenerator http = new HTTPGenerator(HTTP_GET_METHOD, url, header, generateQueryString(indicator), proxy);
        try {
            if(this.tracemode){
                context.getLogger().info("Send the API CALL:\n" + http.getRequest());
            }
            final HttpResponse httpResponse = http.execute();

            if (HttpResponseUtils.isSuccessHttpCode(httpResponse.getStatusLine().getStatusCode()))
            {
                final JSONObject json = getJsonResponse(httpResponse);
                if (json != null)
                {
                    PrometheusResponse prometheusResponse= new GsonBuilder().create().fromJson(json.toString(),PrometheusResponse.class);
                    prometheusResponse.getData().getResult().stream().forEach(prometheusMetric ->
                    {
                        result.addAll(prometheusMetric.toEntries(indicator.getUnit()));
                    });


                }
            } else {
                final String stringResponse = HttpResponseUtils.getStringResponse(httpResponse);
                throw new PrometheusException(httpResponse.getStatusLine().getReasonPhrase() + " - "+ url + " - " + stringResponse);
            }

        } finally {
            http.closeHttpClient();
            return result;
        }
    }

    public List<Entry> getData() throws PrometheusException {
        StringBuilder error=new StringBuilder();
        List<Entry> entryList=new ArrayList<>();
        prometheusIndicators.getIndicatorList().stream().forEach(indicator ->
        {
            try {
                entryList.addAll(getINdicatorsData(indicator));
            } catch (Exception e) {
                error.append("Technical error for "+indicator.getMetricName()+" "+e.getMessage() + " " +e.getCause().getMessage());
            }
        });

        if(error.length()>0)
        {
            throw new PrometheusException(error.toString());
        }

        return entryList;
    }



    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    public String getDataExchangeApiUrl() {
        return dataExchangeApiUrl;
    }

    public void setDataExchangeApiUrl(String dataExchangeApiUrl) {
        this.dataExchangeApiUrl = dataExchangeApiUrl;
    }

    public Optional<String> getProxyName() {
        return proxyName;
    }

    public void setProxyName(Optional<String> proxyName) {
        this.proxyName = proxyName;
    }

    private Map<String,String> generateQueryString(PrometheusIndicator indicator)
    {
        Map<String,String> query=new HashMap<>();
        if(indicator!=null)
        {
            if(indicator.getLabels()!=null)
            {
                StringBuilder labelfilter=new StringBuilder();
                indicator.getLabels().forEach((s, s2) -> {
                        labelfilter.append(s+"=\""+s2+"\",");

                });

                String label=labelfilter.toString();
                if(label.length()>0)
                {
                    label = label.substring(0, label.length() - 1);

                    query.put(PROMETHEUS_QUERY, indicator.getMetricName()+PROMETHEUS_BEGIN_LABEL+label+PROMETHEUS_END_LABEL);
                }
                else
                    query.put(PROMETHEUS_QUERY, indicator.getMetricName());

            }
            else
                query.put(PROMETHEUS_QUERY, indicator.getMetricName());

            query.put(PROMETHEUS_START,String.valueOf(starttime));
            query.put(PROMETHEUS_END,String.valueOf(endtime));
            query.put(PROMETHEUS_STEP,PROMETHEUS_STEP_VALUE);
        }

        return query;
    }

    public PrometheusIndicators getPrometheusIndicators() {
        return prometheusIndicators;
    }

    public void setPrometheusIndicators(PrometheusIndicators prometheusIndicators) {
        this.prometheusIndicators = prometheusIndicators;
    }

    public Long getStarttime() {
        return starttime;
    }

    public void setStarttime(Long starttime) {
        this.starttime = starttime;
    }

    public String getVitualUserID() {
        return vitualUserID;
    }

    public void setVitualUserID(String vitualUserID) {
        this.vitualUserID = vitualUserID;
    }

    public boolean isTracemode() {
        return tracemode;
    }

    public void setTracemode(boolean tracemode) {
        this.tracemode = tracemode;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
