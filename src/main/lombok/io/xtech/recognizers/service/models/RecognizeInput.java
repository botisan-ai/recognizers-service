package io.xtech.recognizers.service.models;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Introspected
@Data
public class RecognizeInput {
    private String text;
    private String culture;
}
