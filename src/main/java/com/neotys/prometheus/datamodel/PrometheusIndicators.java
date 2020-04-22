package com.neotys.prometheus.datamodel;

import java.util.ArrayList;
import java.util.List;

public class PrometheusIndicators {
    private List<PrometheusIndicator> indicatorList=new ArrayList<>();

    public PrometheusIndicators(List<PrometheusIndicator> indicatorList) {
        this.indicatorList = indicatorList;
    }

    public List<PrometheusIndicator> getIndicatorList() {
        return indicatorList;
    }

    public void setIndicatorList(List<PrometheusIndicator> indicatorList) {
        this.indicatorList = indicatorList;
    }
}
