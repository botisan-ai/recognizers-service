package io.xtech.recognizers.service;

import com.google.common.collect.Lists;
import com.microsoft.recognizers.text.ModelResult;
import com.microsoft.recognizers.text.number.NumberRecognizer;
import com.microsoft.recognizers.text.numberwithunit.NumberWithUnitRecognizer;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.xtech.recognizers.bedbath.BedBathRecognizer;
import io.xtech.recognizers.service.models.RecognizeCurrencyRangeInput;
import io.xtech.recognizers.service.models.RecognizeInput;
import io.xtech.recognizers.service.models.RecognizeNumberWithUnitInput;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
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

        return NumberWithUnitRecognizer.recognizeDimension(text, culture)
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

    @Post(value = "/bedbath", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeBedBath(@Body RecognizeNumberWithUnitInput recognizeBedBathInput) {
        String text = recognizeBedBathInput.getText();
        String culture = recognizeBedBathInput.getCulture();
        List<String> units = recognizeBedBathInput.getUnits();

        return BedBathRecognizer.recognizeBedBath(text, culture)
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

    @Post(value = "/currency-range", produces = MediaType.APPLICATION_JSON)
    public List<ModelResult> recognizeCurrencyRange(@Body RecognizeCurrencyRangeInput recognizeCurrencyRangeInput) {
        String text = recognizeCurrencyRangeInput.getText();
        String culture = recognizeCurrencyRangeInput.getCulture();

        List<ModelResult> currencyModelResult = NumberWithUnitRecognizer.recognizeCurrency(text, culture);
        if (currencyModelResult == null || currencyModelResult.size() == 0) {
            // return empty list if no currency found
            return Lists.newArrayList();
        }

        int offset = 0;
        List<ModelResult> convertedCurrencyResult = new ArrayList<>();

        // replace the currency occurrences to numbers
        for (ModelResult result : currencyModelResult) {
            String replacement = result.resolution.get("value").toString();
            int newStart = offset + result.start;

            String newText = text.substring(0, offset + result.start) + replacement + ((offset + result.end + 1 < text.length()) ? text.substring(offset + result.end + 1) : "");
            offset += replacement.length() - text.substring(offset + result.start, offset + result.end + 1).length();

            int newEnd = offset + result.end;
            text = newText;

            SortedMap<String, Object> newResolution = new TreeMap<>(result.resolution);
            newResolution.put("origText", result.text);
            newResolution.put("origStart", result.start);
            newResolution.put("origEnd", result.end);

            convertedCurrencyResult.add(new ModelResult(replacement, newStart, newEnd, result.typeName, newResolution));
        }

        List<ModelResult> rangeModelResult = NumberRecognizer.recognizeNumberRange(text, culture);

        List<ModelResult> convertedRangeResult = new ArrayList<>();

        // re-use offset
        offset = 0;
        for (ModelResult rangeResult : rangeModelResult) {
            // internal offset to update the individual rangeResult
            int internalOffset = 0;
            // re-use text
            String newText = rangeResult.text;
            List<String> units = new ArrayList<>();

            int newStart = offset + rangeResult.start;

            for (ModelResult currencyResult : convertedCurrencyResult) {
                if (currencyResult.start < rangeResult.start || currencyResult.end > rangeResult.end) {
                    continue;
                }

                String replacement = currencyResult.resolution.get("origText").toString();
                int firstEnd = internalOffset + currencyResult.start - rangeResult.start;
                int lastStart = internalOffset + currencyResult.end - rangeResult.start + 1;
                newText = newText.substring(0, firstEnd) + replacement + ((lastStart < newText.length()) ? newText.substring(lastStart) : "");
                internalOffset += replacement.length() - currencyResult.text.length();
                units.add(currencyResult.resolution.get("unit").toString());
            }

            offset += internalOffset;
            int newEnd = offset + rangeResult.end;
            SortedMap<String, Object> newResolution = new TreeMap<>(rangeResult.resolution);
            newResolution.put("unit", units);
            if (recognizeCurrencyRangeInput.isShowCurrencyModelResults()) {
                newResolution.put(
                        "currency",
                        currencyModelResult
                                .stream()
                                .filter(modelResult -> modelResult.start >= newStart && modelResult.end <= newEnd)
                );
            }

            ModelResult newRangeResult = new ModelResult(newText, newStart, newEnd, "currency-range", newResolution);
            convertedRangeResult.add(newRangeResult);
        }

        return convertedRangeResult;
    }
}
