package io.xtech.recognizers.service.models;

import lombok.Data;

@Data
public class RecognizeCurrencyRangeInput extends RecognizeInput {
    private boolean showCurrencyModelResults;
}
