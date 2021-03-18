package io.xtech.recognizers.service;

import com.microsoft.recognizers.text.ModelResult;
import com.microsoft.recognizers.text.number.NumberRecognizer;
import com.microsoft.recognizers.text.numberwithunit.NumberWithUnitRecognizer;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.xtech.recognizers.bedbath.BedBathRecognizer;
import io.xtech.recognizers.range.RangeWithUnitsRecognizer;
import io.xtech.recognizers.service.models.RecognizeRangeWithUnitsInput;
import io.xtech.recognizers.service.models.RecognizeInput;
import io.xtech.recognizers.service.models.RecognizeNumberWithUnitInput;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Controller("/recognize")
@Slf4j
public class RecognizeController {
    @Post(value = "/number", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeNumber(@Body RecognizeInput recognizeInput) {
        return NumberRecognizer.recognizeNumber(recognizeInput.getText(), recognizeInput.getCulture());
    }

    @Post(value = "/currency", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeCurrency(@Body RecognizeInput recognizeInput) {
        return NumberWithUnitRecognizer.recognizeCurrency(recognizeInput.getText(), recognizeInput.getCulture());
    }

    @Post(value = "/numberrange", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeRange(@Body RecognizeInput recognizeInput) {
        return NumberRecognizer.recognizeNumberRange(recognizeInput.getText(), recognizeInput.getCulture());
    }

    @Post(value = "/dimension", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeNumberWithUnit(@Body RecognizeNumberWithUnitInput recognizeNumberWithUnitInput) {
        String text = recognizeNumberWithUnitInput.getText();
        String culture = recognizeNumberWithUnitInput.getCulture();
        List<String> units = recognizeNumberWithUnitInput.getUnits();

        return filterModelResultsByUnits(
                NumberWithUnitRecognizer.recognizeDimension(text, culture),
                units
        );
    }

    @Post(value = "/bedbath", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeBedBath(@Body RecognizeNumberWithUnitInput recognizeBedBathInput) {
        String text = recognizeBedBathInput.getText();
        String culture = recognizeBedBathInput.getCulture();
        List<String> units = recognizeBedBathInput.getUnits();

        return filterModelResultsByUnits(
                BedBathRecognizer.recognizeBedBath(text, culture),
                units
        );
    }

    @Post(value = "/currency-range", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeCurrencyRange(@Body RecognizeRangeWithUnitsInput recognizeRangeWithUnitsInput) {
        String text = recognizeRangeWithUnitsInput.getText();
        String culture = recognizeRangeWithUnitsInput.getCulture();
        boolean showNumbers = recognizeRangeWithUnitsInput.isShowNumbers();
        List<ModelResult> modelResults = NumberWithUnitRecognizer.recognizeCurrency(text, culture);

        return RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                "currency-range",
                text,
                culture,
                modelResults,
                showNumbers
        );
    }

    @Post(value = "/dimension-range", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeDimensionRange(@Body RecognizeRangeWithUnitsInput recognizeRangeWithUnitsInput) {
        String text = recognizeRangeWithUnitsInput.getText();
        String culture = recognizeRangeWithUnitsInput.getCulture();
        List<String> units = recognizeRangeWithUnitsInput.getUnits();
        boolean showNumbers = recognizeRangeWithUnitsInput.isShowNumbers();

        List<ModelResult> modelResults = filterModelResultsByUnits(
                NumberWithUnitRecognizer.recognizeDimension(text, culture),
                units
        );

        return RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                "dimension-range",
                text,
                culture,
                modelResults,
                showNumbers
        );
    }

    @Post(value = "/bedbath-range", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeBedBathRange(@Body RecognizeRangeWithUnitsInput recognizeRangeWithUnitsInput) {
        String text = recognizeRangeWithUnitsInput.getText();
        String culture = recognizeRangeWithUnitsInput.getCulture();
        List<String> units = recognizeRangeWithUnitsInput.getUnits();
        boolean showNumbers = recognizeRangeWithUnitsInput.isShowNumbers();

        List<ModelResult> modelResults = filterModelResultsByUnits(
                BedBathRecognizer.recognizeBedBath(text, culture),
                units
        );

        return RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                "bedbath-range",
                text,
                culture,
                modelResults,
                showNumbers
        );
    }

    private static List<ModelResult> filterModelResultsByUnits(List<ModelResult> modelResults, List<String> units) {
        return modelResults
                .stream()
                .filter(modelResult -> {
                    if (units == null || units.isEmpty()) {
                        return true;
                    }

                    return units.contains(modelResult.resolution.getOrDefault("unit", "").toString());
                })
                .collect(Collectors.toList())
                ;
    }
}
