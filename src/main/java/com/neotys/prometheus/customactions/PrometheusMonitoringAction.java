package com.neotys.prometheus.customactions;

import com.google.common.base.Optional;
import com.neotys.action.argument.Arguments;
import com.neotys.action.argument.Option;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.prometheus.common.PrometheusUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class PrometheusMonitoringAction implements Action {
    private static final String BUNDLE_NAME = "com.neotys.prometheus.customactions.bundle";
    private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayName");
    private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");

    @Override
    public String getType() {
        return "PrometheusMonitoring";
    }

    @Override
    public List<ActionParameter> getDefaultActionParameters() {
        final List<ActionParameter> parameters = new ArrayList<>();

        for (final PrometheusMonitoringOption option : PrometheusMonitoringOption.values()) {
            if (Option.AppearsByDefault.True.equals(option.getAppearsByDefault())) {
                parameters.add(new ActionParameter(option.getName(), option.getDefaultValue(),
                        option.getType()));
            }
        }

        return parameters;
    }

    @Override
    public Class<? extends ActionEngine> getEngineClass() {
        return PrometheusMonitoringActionEngine.class;
    }

    @Override
    public Icon getIcon() {
        return PrometheusUtils.getPrometheusIcon();
    }

    @Override
    public String getDescription() {
        final StringBuilder description = new StringBuilder();
        description.append("The PrometheusMonitoring Action will get metrics from Prometheus.\n")
                .append(Arguments.getArgumentDescriptions(PrometheusMonitoringOption.values()));

        return description.toString();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getDisplayPath() {
        return DISPLAY_PATH;
    }

    @Override
    public Optional<String> getMinimumNeoLoadVersion() {
        return Optional.of("7.2");
    }

    @Override
    public Optional<String> getMaximumNeoLoadVersion() {
        return Optional.absent();
    }
}
