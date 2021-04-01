package io.xtech.recognizers.service.models;

import lombok.Data;

import java.util.List;

@Data
public class RecognizeNumberWithUnitInput extends RecognizeInput {
    private List<String> units;
}
