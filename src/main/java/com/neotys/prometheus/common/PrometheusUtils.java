package com.neotys.prometheus.common;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.neotys.prometheus.datamodel.PrometheusIndicators;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

public class PrometheusUtils
{
    private static final ImageIcon PROMETHEUS_ICON;

    static
    {
        final URL iconURL = PrometheusUtils.class.getResource("prometheus.png");
            if (iconURL != null) {
            PROMETHEUS_ICON = new ImageIcon(iconURL);
            } else {
            PROMETHEUS_ICON = null;
            }
    }

    private PrometheusUtils() {
    }


    public static PrometheusIndicators getIndicators(String jsonindicators) throws FileNotFoundException
    {
        PrometheusIndicators prometheusIndicators;
        Gson gson=new Gson();

        JsonReader reader = new JsonReader(new FileReader(jsonindicators.trim()));
        return prometheusIndicators=gson.fromJson(reader, PrometheusIndicators.class);

    }
    public static ImageIcon getPrometheusIcon() {
        return PROMETHEUS_ICON;
        }
}