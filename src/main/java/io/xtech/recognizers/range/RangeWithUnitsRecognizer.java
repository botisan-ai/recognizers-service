package io.xtech.recognizers.range;

import com.google.common.collect.Lists;
import com.microsoft.recognizers.text.ModelResult;
import com.microsoft.recognizers.text.number.NumberRecognizer;
import io.xtech.recognizers.service.models.RecognizeRangeWithUnitsInput;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class RangeWithUnitsRecognizer {
    public static List<ModelResult> recognizeRangeWithUnits(
            String rangeModelType,
            List<ModelResult> numberWithUnitsResult,
            RecognizeRangeWithUnitsInput recognizeRangeWithUnitsInput
    ) {
        String originalText = recognizeRangeWithUnitsInput.getText();
        String text = originalText;
        String culture = recognizeRangeWithUnitsInput.getCulture();
        boolean showNumbers = recognizeRangeWithUnitsInput.isShowNumbers();

        if (numberWithUnitsResult == null || numberWithUnitsResult.size() == 0) {
            // return empty list if no currency found
            return Lists.newArrayList();
        }

        int offset = 0;
        List<ModelResult> convertedNumberResult = new ArrayList<>();

        // replace the currency occurrences to numbers
        for (ModelResult result : numberWithUnitsResult) {
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

            convertedNumberResult.add(new ModelResult(replacement, newStart, newEnd, result.typeName, newResolution));
        }

        List<ModelResult> rangeModelResult = NumberRecognizer.recognizeNumberRange(text, culture);

        List<ModelResult> convertedRangeResult = new ArrayList<>();

        for (ModelResult rangeResult : rangeModelResult) {
            // internal offset to update the individual rangeResult
            int internalOffset = 0;
            int mergeCount = 0;
            // re-use text
            String newText = rangeResult.text;
            List<String> units = new ArrayList<>();

            for (ModelResult numberResult : convertedNumberResult) {
                if (numberResult.start < rangeResult.start || numberResult.end > rangeResult.end) {
                    continue;
                }

                String replacement = numberResult.resolution.get("origText").toString();
                int firstEnd = internalOffset + numberResult.start - rangeResult.start;
                int lastStart = internalOffset + numberResult.end - rangeResult.start + 1;
                newText = newText.substring(0, firstEnd) + replacement + ((lastStart < newText.length()) ? newText.substring(lastStart) : "");
                internalOffset += replacement.length() - numberResult.text.length();
                units.add(numberResult.resolution.get("unit").toString());
                mergeCount += 1;
            }

            if (mergeCount <= 0) {
                continue;
            }

            // calculate the start and end index
            int newStart = originalText.indexOf(newText);
            int newEnd = newStart + newText.length() - 1;

            SortedMap<String, Object> newResolution = new TreeMap<>(rangeResult.resolution);
            newResolution.put("unit", units);
            if (showNumbers) {
                newResolution.put(
                        "numbers",
                        numberWithUnitsResult
                                .stream()
                                .filter(modelResult -> modelResult.start >= newStart && modelResult.end <= newEnd)
                );
            }

            ModelResult newRangeResult = new ModelResult(newText, newStart, newEnd, rangeModelType, newResolution);
            convertedRangeResult.add(newRangeResult);
        }

        return convertedRangeResult;
    }
}
