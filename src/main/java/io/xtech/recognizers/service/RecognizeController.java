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
import io.xtech.recognizers.service.models.RecognizeCombinedInput;
import io.xtech.recognizers.service.models.RecognizeRangeWithUnitsInput;
import io.xtech.recognizers.service.models.RecognizeInput;
import io.xtech.recognizers.service.models.RecognizeNumberWithUnitInput;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller("/recognize")
@Slf4j
public class RecognizeController {
    @Post(value = "/combined", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeCombined(@Body RecognizeCombinedInput recognizeCombinedInput) {
        List<String> entities = recognizeCombinedInput.getEntities();

        List<ModelResult> numberResults = new ArrayList<>();
        List<ModelResult> currencyResults = new ArrayList<>();
        List<ModelResult> dimensionResults = new ArrayList<>();
        List<ModelResult> bedBathResults = new ArrayList<>();

        List<ModelResult> numberRangeResults = new ArrayList<>();
        List<ModelResult> currencyRangeResults = new ArrayList<>();
        List<ModelResult> dimensionRangeResults = new ArrayList<>();
        List<ModelResult> bedBathRangeResults = new ArrayList<>();


        if (entities == null || entities.contains("number")) {
            numberResults.addAll(recognizeNumber(recognizeCombinedInput));
        }

        if (entities == null || entities.contains("currency") || entities.contains("currency-range")) {
            currencyResults.addAll(recognizeCurrency(recognizeCombinedInput));
        }

        if (entities == null || entities.contains("dimension") || entities.contains("dimension-range")) {
            dimensionResults.addAll(recognizeDimension(recognizeCombinedInput));
        }

        if (entities == null || entities.contains("bedbath") || entities.contains("bedbath-range")) {
            bedBathResults.addAll(recognizeBedBath(recognizeCombinedInput));
        }

        if (entities == null || entities.contains("numberrange")) {
            numberRangeResults.addAll(recognizeNumberRange(recognizeCombinedInput));
        }

        if (entities == null || entities.contains("currency-range")) {
            currencyRangeResults.addAll(RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                    "currency-range",
                    currencyResults,
                    recognizeCombinedInput
            ));
        }

        if (entities == null || entities.contains("dimension-range")) {
            dimensionRangeResults.addAll(RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                    "dimension-range",
                    dimensionResults,
                    recognizeCombinedInput
            ));
        }

        if (entities == null || entities.contains("bedbath-range")) {
            bedBathRangeResults.addAll(RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                    "bedbath-range",
                    bedBathResults,
                    recognizeCombinedInput
            ));
        }

        List<ModelResult> finalResults = Stream
                .of(
                        currencyRangeResults,
                        dimensionRangeResults,
                        bedBathRangeResults,
                        numberRangeResults,
                        currencyResults,
                        dimensionResults,
                        bedBathResults,
                        numberResults
                )
                .flatMap(Collection::stream)
                .reduce(
                        new ArrayList<>(),
                        (results, modelResult) -> {
                            if (!recognizeCombinedInput.isMergeResults()) {
                                results.add(modelResult);
                                return results;
                            }

                            boolean containsResult = results
                                    .stream()
                                    .anyMatch(
                                            result -> modelResult.start >= result.start &&
                                                      modelResult.end <= result.end)
                                    ;

                            if (!containsResult) {
                                results.add(modelResult);
                            }

                            return results;
                        },
                        (resultsA, resultsB) -> {
                            if (!recognizeCombinedInput.isMergeResults()) {
                                resultsA.addAll(resultsB);
                                return resultsA;
                            }

                            List<ModelResult> newRsults = resultsB
                                    .stream()
                                    .filter(resultB -> {
                                        if (!recognizeCombinedInput.isMergeResults()) {
                                            return true;
                                        }

                                        boolean containsResult = resultsA
                                                .stream()
                                                .anyMatch(
                                                        resultA -> resultB.start >= resultA.start &&
                                                                resultB.end <= resultA.end)
                                                ;

                                        return !containsResult;
                                    })
                                    .collect(Collectors.toList());

                            resultsA.addAll(newRsults);
                            return resultsA;
                        }
                );

        return finalResults;
    }

    @Post(value = "/number", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeNumber(@Body RecognizeInput recognizeInput) {
        return NumberRecognizer.recognizeNumber(recognizeInput.getText(), recognizeInput.getCulture());
    }

    @Post(value = "/numberrange", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeNumberRange(@Body RecognizeInput recognizeInput) {
        return NumberRecognizer.recognizeNumberRange(recognizeInput.getText(), recognizeInput.getCulture());
    }

    @Post(value = "/currency", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeCurrency(@Body RecognizeNumberWithUnitInput recognizeNumberWithUnitInput) {
        String text = recognizeNumberWithUnitInput.getText();
        String culture = recognizeNumberWithUnitInput.getCulture();
        List<String> units = recognizeNumberWithUnitInput.getUnits();

        return filterModelResultsByUnits(
                NumberWithUnitRecognizer.recognizeCurrency(text, culture),
                units
        );
    }

    @Post(value = "/dimension", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeDimension(@Body RecognizeNumberWithUnitInput recognizeNumberWithUnitInput) {
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
        List<ModelResult> modelResults = recognizeCurrency(recognizeRangeWithUnitsInput);
        return RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                "currency-range",
                modelResults,
                recognizeRangeWithUnitsInput
        );
    }

    @Post(value = "/dimension-range", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeDimensionRange(@Body RecognizeRangeWithUnitsInput recognizeRangeWithUnitsInput) {
        List<ModelResult> modelResults = recognizeDimension(recognizeRangeWithUnitsInput);
        return RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                "dimension-range",
                modelResults,
                recognizeRangeWithUnitsInput
        );
    }

    @Post(value = "/bedbath-range", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeBedBathRange(@Body RecognizeRangeWithUnitsInput recognizeRangeWithUnitsInput) {
        List<ModelResult> modelResults = recognizeBedBath(recognizeRangeWithUnitsInput);
        return RangeWithUnitsRecognizer.recognizeRangeWithUnits(
                "bedbath-range",
                modelResults,
                recognizeRangeWithUnitsInput
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
