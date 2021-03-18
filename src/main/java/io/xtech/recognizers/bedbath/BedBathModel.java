package io.xtech.recognizers.bedbath;

import com.microsoft.recognizers.text.IExtractor;
import com.microsoft.recognizers.text.IParser;
import com.microsoft.recognizers.text.numberwithunit.models.AbstractNumberWithUnitModel;

import java.util.Map;

public class BedBathModel extends AbstractNumberWithUnitModel {
    public static final String MODEL_TYPE = "bedbath";

    public BedBathModel(Map<IExtractor, IParser> extractorParserMap) {
        super(extractorParserMap);
    }

    @Override
    public String getModelTypeName() {
        return MODEL_TYPE;
    }
}
