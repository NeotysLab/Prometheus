<p align="center"><img src="/screenshots/prometheus.png" width="40%" alt="Prometheus Logo" /></p>

# Prometheus	Integration for NeoLoad (experimental)

## Overview

These Advanced Actions allows you to integrate [NeoLoad](https://www.neotys.com/neoload/overview) with [Prometheus](https://prometheus.io/) in order to collect monitoring data.

 
* **PrometheusMonitoring**   
    * **Prometheus &rarr; NeoLoad**: Retrieves counters from prometheus and inserts them in NeoLoad External Data so that
      you can correlate NeoLoad and Prometheus metrics within NeoLoad.
      
      The metrics collected need to describe in json config files precising the metrics that you would like to collect from prometheus.
 ```json 
      { 
        indicatorList : [
      		{
      			metricName :"1st prometheus metric name ",
      			labels : 
      				{
      				  job: "prometheus",
      				  quantile : "=0.25",
      				  label3 :"=value",
      				  label4 :"=~val.*"
      				  .....
      				},
      			unit:"s"
      		},
      	{
              			metricName :"2sd prometheus metric name ",
              			labels : 
              				{
              				  job: "prometheus",
              				  quantile : "=0.25",
              				  label3 :"!=value",
              				  label4 :"=~va.*"
              				  .....
              				},
              			unit:"s"
              		}
      	]
      
      }
```
the filter label can use several type of operator ( compatible with prometheus):
* =: Select labels that are exactly equal to the provided string.
* !=: Select labels that are not equal to the provided string.
* =~: Select labels that regex-match the provided string.
* !~: Select labels that do not regex-match the provided string.
if you are precizing any operator in the value of the label than NeoLaod will apply the default operator : `=`

In order to make sure to make this configuration file available on each Load Generator machine, We recommand to store this file in the `custom-ressources` folder of your NeoLoad Project
   <p align="center"><img src="/screenshots/folder.png" alt="Prometheus project folder" /></p>

Neoload will collect the data related to the metrics described in this json config file.

| Property | Value |
| -----| -------------- |
| Maturity | Experimental |
| Author   | Neotys Partner Team |
| License  | [BSD Simplified](https://www.neotys.com/documents/legal/bsd-neotys.txt) |
| NeoLoad  | 7.2+ (Enterprise or Professional Edition w/ Integration & Advanced Usage and NeoLoad Web option required)|
| Requirements | NeoLoad Web |
| Bundled in NeoLoad | No
| Download Binaries | <ul><li>[latest release](https://github.com/neotyslab/Prometheus/releases/latest) is only compatible with NeoLoad from version 6.7</li><li> Use this [release](https://github.com/Neotys-Labs/Dynatrace/releases/tag/Neotys-Labs%2FDynatrace.git-2.0.10) for previous NeoLoad versions</li></ul>|

## Installation

1. Download the [latest release](https://github.com/neotyslab/Prometheus/releases/latest) for NeoLoad from version 7.2
1. Read the NeoLoad documentation to see [How to install a custom Advanced Action](https://www.neotys.com/documents/doc/neoload/latest/en/html/#25928.htm).

<p align="center"><img src="/screenshots/advanced_action.png" alt="Prometheus Advanced Action" /></p>

## NeoLoad Set-up

Once installed, how to use in a given NeoLoad project:

1. Create a “Prometheus” User Path.
1. Insert "PrometheusMonitoring" in the ‘Actions’ block.
   <p align="center"><img src="/screenshots/vu.png" alt="Prometheus User Path" /></p>
1. Select the **Actions** container and set a pacing duration of 20 seconds.
   <p align="center"><img src="/screenshots/pacing.png" alt="Action's Pacing" /></p>
1. Select the **Actions** container and set the "Reset user session and emulate new browser between each iteration" runtime parameters to "No".
1. Create a "PopulationPrometheus" Population that contains 100% of "Prometheus" User Path.
   <p align="center"><img src="/screenshots/population.png" alt="Prometheus Population" /></p>
1. In the **Runtime** section, select your scenario, select the "PopulationPrometheus" population and define a constant load of 1 user for the full duration of the load test.
   <p align="center"><img src="/screenshots/scenario.png" alt="Load Variation Policy" /></p>
1. Do not use multiple load generators. Good practice should be to keep only the local one.



## Parameters for Prometheus Monitoring

Tip: Get NeoLoad API information in NeoLoad preferences: Project Preferences / REST API.

| Name             | Description |
| -----            | ----- |
| PrometheusHost      | Prometheus hostname or IP |
| PrometheusPort  | Port of you prometheus server|
| NeoLoadJsonMetricFile  | Path to the json configuration file |
| SSL  | Define if prometheus requires to use ssl . Value possible True/False |
| dataExchangeApiKey (Optional)  | API key of the DataExchange API |
| proxyName (Optional) |  The name of the NeoLoad proxy to access to Dynatrace |
| Tracemode (Optional) |  Enable to trace logs of each request send to Prometheus |


  

## Status Codes
* Prometheus monitoring
    * NL-PROMETHEUS_MONITORING_ACTION-01: Could not parse arguments
    * NL-PROMETHEUS_MONITORING_ACTION-02: Technical Error
    * NL-PROMETHEUS_MONITORING_ACTION-03: Bad context
