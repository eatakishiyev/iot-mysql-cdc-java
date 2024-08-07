package iot.cdc.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumPayload {
    private Payload payload;


    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public static class Payload {
        private Map<String, String> after;
        private Map<String,String> source;

        public Map<String, String> getAfter() {
            return after;
        }

        public void setAfter(Map<String, String> after) {
            this.after = after;
        }

        public Map<String, String> getSource() {
            return source;
        }

        public void setSource(Map<String, String> source) {
            this.source = source;
        }
    }
}

