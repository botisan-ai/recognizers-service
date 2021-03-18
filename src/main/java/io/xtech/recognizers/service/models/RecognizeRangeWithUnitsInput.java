package io.xtech.recognizers.service.models;

import lombok.Data;

@Data
public class RecognizeRangeWithUnitsInput extends RecognizeNumberWithUnitInput {
    private boolean showNumbers;
}
