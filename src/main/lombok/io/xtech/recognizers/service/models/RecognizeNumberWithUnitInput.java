package io.xtech.recognizers.service.models;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Introspected
@EqualsAndHashCode(callSuper = true)
@Data
public class RecognizeNumberWithUnitInput extends RecognizeInput {
    private List<String> units;
}
