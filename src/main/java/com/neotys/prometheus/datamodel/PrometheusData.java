package com.neotys.prometheus.datamodel;

import com.neotys.rest.dataexchange.model.EntryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrometheusData {
    //"data": {
    //    //        "resultType": "matrix",
    //    //        "result": [
    //    //            {
    //    //                "metric": {
    //    //                    "__name__": "go_gc_duration_seconds",
    //    //                    "instance": "localhost:9090",
    //    //                    "job": "prometheus",
    //    //                    "quantile": "0.25"
    //    //                },
    //    //                "values": [
    //    //                    [
    //    //                        1587449400,
    //    //                        "0.00002778"
    //    //                    ],
    //    //                    [
    //    //                        1587449405,
    //    //                        "0.00002778"
    //    //                    ],
    //    //                    [
    //    //                        1587449410,
    //    //}
// "matrix" | "vector" | "scalar" | "string",
    private String resultType;
    private List<PrometheusMetric> result;


    public PrometheusData(String resultType, List<PrometheusMetric> result) {
        this.resultType = resultType;
        this.result = result;
    }

    public TypeEnum getResultType() {
        return TypeEnum.valueOf(resultType);
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List<PrometheusMetric> getResult() {
        return result;
    }

    public void setResult(List<PrometheusMetric> result) {
        this.result = result;
    }

    public enum TypeEnum {
        MATRIX("matrix"),
        VECTOR("vector"),
        STRING("string"),
        SCALAR("scalar");

        private String value;

        TypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static TypeEnum fromValue(String text) {
            for (TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }


}
