package io.xtech.recognizers.service.models;

import io.micronaut.core.annotation.Introspected;
import lombok.Data;

@Introspected
@Data
public class RecognizeInput {
    private String text;
    private String culture;

//    public RecognizeInput() {
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public String getCulture() {
//        return culture;
//    }
//
//    public void setCulture(String culture) {
//        this.culture = culture;
//    }
}
