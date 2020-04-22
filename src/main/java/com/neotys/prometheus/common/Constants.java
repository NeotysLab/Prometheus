package com.neotys.prometheus.common;

public final class Constants {

	/*** Dynatrace ***/
	public static final String PROMETHEUS = "Prometheus";

	/*** NeoLoad context (Data Exchange API) ***/
	public static final String NEOLOAD_CONTEXT_HARDWARE = PROMETHEUS;
	public static final String NEOLOAD_CONTEXT_LOCATION = PROMETHEUS;
	public static final String NEOLOAD_CONTEXT_SOFTWARE = PROMETHEUS;
	public static final String PROMETHEUS_API_PROTOCOL="http://";
	/*** NeoLoad Current Virtual user context (Keep object in cache cross iterations) ***/
	public static final String NL_DATA_EXCHANGE_API_CLIENT = "NLDataExchangeAPIClient";

    public static final String TRACE_MODE = "traceMode";

	public static final String PROMETHEUS_LAST_EXECUTION_TIME = "PrometheusLastExecutionTime";

	public static final String PROMETHEUS_QUERY_PATH="/api/v1/query_range";

	//----time in second-----
	public static final int PROMETHEUS_MONITORING_RANGE=10;

	public static final String PROMETHEUS_QUERY="query";
	public static final String PROMETHEUS_START="start";
	public static final String PROMETHEUS_END="end";
	public static final String PROMETHEUS_STEP="step";
	public static final String PROMETHEUS_STEP_VALUE="5s";


	public static final String PROMETHEUS_BEGIN_LABEL="{";
	public static final String PROMETHEUS_END_LABEL="}";


}
