package io.xtech.recognizers.service.models;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Introspected
@EqualsAndHashCode(callSuper = true)
@Data
public class RecognizeCombinedInput extends RecognizeRangeWithUnitsInput {
    private List<String> entities;
    private boolean mergeResults;

//    public RecognizeCombinedInput() {
//        super();
//    }
//
//    public List<String> getEntities() {
//        return entities;
//    }
//
//    public void setEntities(List<String> entities) {
//        this.entities = entities;
//    }
//
//    public boolean isMergeResults() {
//        return mergeResults;
//    }
//
//    public void setMergeResults(boolean mergeResults) {
//        this.mergeResults = mergeResults;
//    }
}
