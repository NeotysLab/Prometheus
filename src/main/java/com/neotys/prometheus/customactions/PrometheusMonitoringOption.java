package com.neotys.prometheus.customactions;

import com.neotys.action.argument.ArgumentValidator;
import com.neotys.action.argument.Option;
import com.neotys.extensions.action.ActionParameter;

import static com.neotys.action.argument.DefaultArgumentValidator.BOOLEAN_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.INTEGER_VALIDATOR;
import static com.neotys.action.argument.DefaultArgumentValidator.NON_EMPTY;
import static com.neotys.action.argument.Option.AppearsByDefault.False;
import static com.neotys.action.argument.Option.AppearsByDefault.True;
import static com.neotys.action.argument.Option.OptionalRequired.Required;
import static com.neotys.action.argument.Option.OptionalRequired.Optional;
import static com.neotys.extensions.action.ActionParameter.Type.TEXT;

enum PrometheusMonitoringOption implements Option {
    PrometheusHost("PrometheusHost", Required, True, TEXT,
            "Your prometheus end point",
                    "The Prometheus host or ip",
                NON_EMPTY),
    PrometheusPort("PrometheusPort", Required, True, TEXT,
            "80",
            "Dynatrace ID (section of your Dynatrace saas url).",
            INTEGER_VALIDATOR),
    NeoLoadJsonMetricFile("NeoLoadJsonMetricFile", Required, True, TEXT,
            "${NL-CustomResources}/yourjsonfile.json",
                    "Path to the file describing the metrics to query",
                    NON_EMPTY),
    SSL("SSL", Required, True, TEXT,
            "false",
            "Boolean that will configure neoload to utilise SSL to interact with the prometheus API",
            BOOLEAN_VALIDATOR),
    NeoLoadProxy("NeoLoadProxy", Optional, False, TEXT,
            "proxy name defined in NeoLoad settings",
            "Proxy named defined in the NeoLoad settings",
            NON_EMPTY),
    NeoLoadDataExchangeApiKey("NeoLoadDataExchangeApiKey", Optional, False, TEXT,
            "Dataexchange API key defined in NeoLoad",
            "Dataexchange API key defined in NeoLoad",
            NON_EMPTY),
    TraceMode("Tracemode", Optional, False, TEXT,
            "False",
                    "Tracemode will enable logging of the api calls done agains prometeus. Value possible : True /False",
              BOOLEAN_VALIDATOR);


    private final String name;
    private final Option.OptionalRequired optionalRequired;
    private final Option.AppearsByDefault appearsByDefault;
    private final ActionParameter.Type type;
    private final String defaultValue;
    private final String description;
    private final ArgumentValidator argumentValidator;

    PrometheusMonitoringOption(final String name, final Option.OptionalRequired optionalRequired,
                              final Option.AppearsByDefault appearsByDefault,
                              final ActionParameter.Type type, final String defaultValue, final String description,
                              final ArgumentValidator argumentValidator) {
        this.name = name;
        this.optionalRequired = optionalRequired;
        this.appearsByDefault = appearsByDefault;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
        this.argumentValidator = argumentValidator;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Option.OptionalRequired getOptionalRequired() {
        return optionalRequired;
    }

    @Override
    public Option.AppearsByDefault getAppearsByDefault() {
        return appearsByDefault;
    }

    @Override
    public ActionParameter.Type getType() {
        return type;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ArgumentValidator getArgumentValidator() {
        return argumentValidator;
    }
}

