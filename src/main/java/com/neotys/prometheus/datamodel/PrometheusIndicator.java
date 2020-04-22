package com.neotys.prometheus.datamodel;

import java.util.HashMap;

public class PrometheusIndicator {
    private String metricName=null;
    private HashMap<String,String> labels=new HashMap<>();
    private String unit=null;


    public PrometheusIndicator(String metricName, HashMap<String, String> labels, String unit) {
        this.metricName = metricName;
        this.labels = labels;
        this.unit = unit;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public HashMap<String, String> getLabels() {
        return labels;
    }

    public void setLabels(HashMap<String, String> labels) {
        this.labels = labels;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
