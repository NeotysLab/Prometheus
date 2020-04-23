package com.neotys.prometheus.datamodel;

import com.neotys.rest.dataexchange.model.EntryBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.neotys.prometheus.common.Constants.PROMETHEUS;

public class PrometheusMetric {
    private HashMap<String,String> metric;
    private Map<Long,Double> values;

    public PrometheusMetric(HashMap<String, String> metric, Map<Long, Double> values
    ) {
        this.metric=metric;
        this.values = values;
    }

    public String getMetricName()
    {
        if(metric!=null)
        {
            if(metric.containsKey("__name__"))
                return metric.get("__name__");
            else
                return null;
        }
        else
            return null;
    }

    public List<String> getPath()
    {
        return metric.entrySet().stream().filter(stringStringEntry ->
            !stringStringEntry.getKey().equalsIgnoreCase("__name__")
        ).map(stringStringEntry -> { return stringStringEntry.getKey()+":"+stringStringEntry.getValue();}).collect(Collectors.toList());
    }

    public List<com.neotys.rest.dataexchange.model.Entry> toEntries(String unit) {
        return values.entrySet().stream()
                .map(longDoubleEntry -> {
                    return toEntry(longDoubleEntry.getKey(),longDoubleEntry.getValue(),unit);
                })
                .collect(Collectors.toList());
    }

    private com.neotys.rest.dataexchange.model.Entry toEntry(Long time,Double value,String unit) {
        List<String> path = new ArrayList<>();
        path.add(PROMETHEUS);
         path.addAll(this.getPath());
        path.add(this.getMetricName());

        return new EntryBuilder(path, time*1000)
                .unit(unit)
                .value(value)
                .build();
    }

    public HashMap<String, String> getLabel() {
        return metric;
    }

    public void setLabel(HashMap<String, String> label) {
        this.metric = label;
    }

    public Map<Long, Double> getValues() {
        return values;
    }

    public void setValues(Map<Long, Double> values) {
        this.values = values;
    }
}
