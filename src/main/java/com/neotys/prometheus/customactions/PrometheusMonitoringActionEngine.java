package com.neotys.prometheus.customactions;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neotys.action.result.ResultFactory;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.Logger;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.prometheus.common.Constants;
import com.neotys.prometheus.common.PrometheusException;
import com.neotys.prometheus.common.PrometheusPlugin;
import com.neotys.prometheus.common.PrometheusUtils;
import com.neotys.prometheus.datamodel.PrometheusIndicators;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClient;
import com.neotys.rest.dataexchange.client.DataExchangeAPIClientFactory;
import com.neotys.rest.dataexchange.model.ContextBuilder;
import com.neotys.rest.error.NeotysAPIException;
import org.apache.olingo.odata2.api.exception.ODataException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.emptyToNull;
import static com.neotys.action.argument.Arguments.getArgumentLogString;
import static com.neotys.action.argument.Arguments.parseArguments;
import static com.neotys.prometheus.common.Constants.PROMETHEUS_MONITORING_RANGE;

public class PrometheusMonitoringActionEngine implements ActionEngine {

    private static final String STATUS_CODE_INVALID_PARAMETER = "NL-PROMETHEUS_MONITORING_ACTION-01";
    private static final String STATUS_CODE_TECHNICAL_ERROR = "NL-PROMETHEUS_MONITORING_ACTION-02";
    private static final String STATUS_CODE_BAD_CONTEXT = "NL-PROMETHEUS_MONITORING_ACTION-03";


    @Override
    public SampleResult execute(final Context context, final List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();


        final Map<String, Optional<String>> parsedArgs;
        try {
            parsedArgs = parseArguments(parameters, PrometheusMonitoringOption.values());
        } catch (final IllegalArgumentException iae) {
            return ResultFactory.newErrorResult(context, STATUS_CODE_INVALID_PARAMETER, "Could not parse arguments: ", iae);
        }

        if (context.getWebPlatformRunningTestUrl() == null) {
            return ResultFactory.newErrorResult(context, STATUS_CODE_BAD_CONTEXT, "Bad context: ", new PrometheusException("No NeoLoad Web test is running"));
        }

        final Logger logger = context.getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug("Executing " + this.getClass().getName() + " with parameters: "
                    + getArgumentLogString(parsedArgs, PrometheusMonitoringOption.values()));
        }

        final String prometheussHost = parsedArgs.get(PrometheusMonitoringOption.PrometheusHost.getName()).get();
        final String prometheusPort = parsedArgs.get(PrometheusMonitoringOption.PrometheusPort.getName()).get();
        final String prometheusFile = parsedArgs.get(PrometheusMonitoringOption.NeoLoadJsonMetricFile.getName()).get();
        final String ssl=parsedArgs.get(PrometheusMonitoringOption.SSL.getName()).get();

        final Optional<String> optionalTraceMode = parsedArgs.get(PrometheusMonitoringOption.TraceMode.getName());
        final Optional<String> proxyName = parsedArgs.get(PrometheusMonitoringOption.NeoLoadProxy.getName());
        final Optional<String> basicUser = parsedArgs.get(PrometheusMonitoringOption.BasicAuthUser.getName());
        final Optional<String> basicpwd = parsedArgs.get(PrometheusMonitoringOption.BasicAuthPWD.getName());

        final Optional<String> dataExchangeApiKey = parsedArgs.get(PrometheusMonitoringOption.NeoLoadDataExchangeApiKey.getName());


        try {

            // Check last execution time (and fail if called less than 45 seconds ago).
            final Object prometheusMonitoringTime = context.getCurrentVirtualUser().get(Constants.PROMETHEUS_LAST_EXECUTION_TIME);
            final Long prometheusCurrentExecution = System.currentTimeMillis();

            if(!(prometheusMonitoringTime instanceof Long)){
                requestBuilder.append("(first execution).\n");
            } else if(((Long)prometheusMonitoringTime + 15)*1000 > prometheusCurrentExecution){
                return ResultFactory.newErrorResult(context, STATUS_CODE_BAD_CONTEXT, "Bad context: Not enough delay between the two Prometheus advanced action execution. Make sure to have at least 20 seconds pacing on the Actions container.");
            } else {
                requestBuilder.append("(last execution was " + ((prometheusCurrentExecution - (Long)prometheusMonitoringTime)/1000) + " seconds ago)\n");
            }


            final String virtualUserId = context.getCurrentVirtualUser().getId();
            boolean traceMode = optionalTraceMode.isPresent() && Boolean.valueOf(optionalTraceMode.get());
            boolean boolssl=Boolean.valueOf(ssl);

            PrometheusPlugin pluginData= PrometheusPlugin.getInstance(prometheussHost,prometheusPort,PrometheusUtils.getIndicators(prometheusFile),(Long)prometheusMonitoringTime,virtualUserId,context,getDefaultDataExchangeApiUrl(context),proxyName,boolssl,traceMode,basicUser,basicpwd);



            // if therer is multiple virtual user handling the action return error
           if (pluginData != null && !pluginData.getVitualUserID().equals(virtualUserId)) {
                return ResultFactory.newErrorResult(context, STATUS_CODE_BAD_CONTEXT, "Bad context: ", new PrometheusException("Multiple VU on action"));
            }

            sampleResult.sampleStart();
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            long startTs = now.toInstant().toEpochMilli() - context.getElapsedTime();
            logger.debug("Sending start test...");


            /*
             * Handle Dynatrace Timeseries
             */
            // Retrieve DataExchangeAPIClient from Context, or instantiate new one
            DataExchangeAPIClient dataExchangeAPIClient = getDataExchangeAPIClient(context, requestBuilder, getDefaultDataExchangeApiUrl(context), dataExchangeApiKey);
            dataExchangeAPIClient.addEntries( pluginData.getData());
            context.getCurrentVirtualUser().put(Constants.PROMETHEUS_LAST_EXECUTION_TIME, pluginData.getStarttime()+PROMETHEUS_MONITORING_RANGE);


            //first call send event to dynatrace
            sampleResult.sampleEnd();
        } catch (Exception e) {
            return ResultFactory.newErrorResult(context, STATUS_CODE_TECHNICAL_ERROR, "Error encountered :", e);
        }

        sampleResult.setRequestContent(requestBuilder.toString());
        sampleResult.setResponseContent(responseBuilder.toString());
        return sampleResult;
    }

    private String getDefaultDataExchangeApiUrl(final Context context) {
        return "http://" + context.getControllerIp() + ":7400/DataExchange/v1/Service.svc/";
    }

    private boolean validateAgggregationType(Optional<String> aggregatetype)
    {
        ImmutableList<String> dynatraceAggregagtionTypes=ImmutableList.of("MIN","MAX","SUM","AVG","MEDIAN","COUNT","PERCENTILE");
        if(aggregatetype.isPresent())
        {
            if(dynatraceAggregagtionTypes.contains(aggregatetype.get()))
                return true;
        }
        return false;
    }



    private DataExchangeAPIClient getDataExchangeAPIClient(final Context context, final StringBuilder requestBuilder, final String dataExchangeApiUrl, final Optional<String> dataExchangeApiKey) throws GeneralSecurityException, IOException, ODataException, URISyntaxException, NeotysAPIException, NeotysAPIException, ODataException {
        DataExchangeAPIClient dataExchangeAPIClient = (DataExchangeAPIClient) context.getCurrentVirtualUser().get(Constants.NL_DATA_EXCHANGE_API_CLIENT);
        if (dataExchangeAPIClient == null) {
            final ContextBuilder contextBuilder = new ContextBuilder();
            contextBuilder.hardware(Constants.NEOLOAD_CONTEXT_HARDWARE).location(Constants.NEOLOAD_CONTEXT_LOCATION).software(
                    Constants.NEOLOAD_CONTEXT_SOFTWARE).script("PrometheusMonitoring" + System.currentTimeMillis());
            dataExchangeAPIClient = DataExchangeAPIClientFactory.newClient(dataExchangeApiUrl,
                    contextBuilder.build(),
                    dataExchangeApiKey.orNull());
            context.getCurrentVirtualUser().put(Constants.NL_DATA_EXCHANGE_API_CLIENT, dataExchangeAPIClient);
            requestBuilder.append("DataExchangeAPIClient created.\n");
        } else {
            requestBuilder.append("DataExchangeAPIClient retrieved from User Path Context.\n");
        }
        return dataExchangeAPIClient;
    }

    @Override
    public void stopExecute() {

    }

}