package io.xtech.recognizers.service.models;

import lombok.Data;

@Data
public class RecognizeInput {
    private String text;
    private String culture;
}
