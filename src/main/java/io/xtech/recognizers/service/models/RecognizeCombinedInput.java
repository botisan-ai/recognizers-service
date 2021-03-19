package io.xtech.recognizers.service.models;

import lombok.Data;

import java.util.List;

@Data
public class RecognizeCombinedInput extends RecognizeRangeWithUnitsInput {
    private List<String> entities;
    private boolean mergeResults;
}
