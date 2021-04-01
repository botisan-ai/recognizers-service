package io.xtech.recognizers.service.models;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Introspected
@EqualsAndHashCode(callSuper = true)
@Data
public class RecognizeRangeWithUnitsInput extends RecognizeNumberWithUnitInput {
    private boolean showNumbers;

//    public RecognizeRangeWithUnitsInput() {
//        super();
//    }
//
//    public boolean isShowNumbers() {
//        return showNumbers;
//    }
//
//    public void setShowNumbers(boolean showNumbers) {
//        this.showNumbers = showNumbers;
//    }
}
