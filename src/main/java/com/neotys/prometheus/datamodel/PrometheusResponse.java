package com.neotys.prometheus.datamodel;

import java.util.List;

public class PrometheusResponse {
    //{
    //  "status": "success" | "error",
    //  "data": <data>,
    //
    //  // Only set if status is "error". The data field may still hold
    //  // additional data.
    //  "errorType": "<string>",
    //  "error": "<string>",
    //
    //  // Only if there were warnings while executing the request.
    //  // There will still be data in the data field.
    //  "warnings": ["<string>"]

    //{
    //    "status": "success",
    //    "data": {
    //        "resultType": "matrix",
    //        "result": [
    //            {
    //                "metric": {
    //                    "__name__": "go_gc_duration_seconds",
    //                    "instance": "localhost:9090",
    //                    "job": "prometheus",
    //                    "quantile": "0.25"
    //                },
    //                "values": [
    //                    [
    //                        1587449400,
    //                        "0.00002778"
    //                    ],
    //                    [
    //                        1587449405,
    //                        "0.00002778"
    //                    ],
    //                    [
    //                        1587449410,
    //}

    private String status;
    //--only if error----------
    private String errortype;
    private String error;
    //---only if warning
    private List<String> warnings;

    private PrometheusData data;


    public PrometheusResponse(String status, String errortype, String error, List<String> warnings, PrometheusData data) {
        this.status = status;
        this.errortype = errortype;
        this.error = error;
        this.warnings = warnings;
        this.data = data;
    }

    public StatusEnum getStatus() {
        return StatusEnum.valueOf(status);
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrortype() {
        return errortype;
    }

    public void setErrortype(String errortype) {
        this.errortype = errortype;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public PrometheusData getData() {
        return data;
    }

    public void setData(PrometheusData data) {
        this.data = data;
    }

    public enum StatusEnum {
        SUCESS("success"),
        ERROR("error");

        private String value;

        StatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static StatusEnum fromValue(String text) {
            for (StatusEnum b : StatusEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}
