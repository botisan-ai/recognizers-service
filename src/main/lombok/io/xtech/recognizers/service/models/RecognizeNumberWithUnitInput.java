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

//    public RecognizeNumberWithUnitInput() {
//        super();
//    }
//
//    public List<String> getUnits() {
//        return units;
//    }
//
//    public void setUnits(List<String> units) {
//        this.units = units;
//    }
}
