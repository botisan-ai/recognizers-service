package io.xtech.recognizers.bedbath;

import com.google.common.collect.ImmutableMap;
import com.microsoft.recognizers.text.Culture;
import com.microsoft.recognizers.text.IModel;
import com.microsoft.recognizers.text.ModelResult;
import com.microsoft.recognizers.text.numberwithunit.NumberWithUnitOptions;
import com.microsoft.recognizers.text.numberwithunit.NumberWithUnitRecognizer;
import com.microsoft.recognizers.text.numberwithunit.extractors.NumberWithUnitExtractor;
import com.microsoft.recognizers.text.numberwithunit.parsers.NumberWithUnitParser;

import java.util.List;

public class BedBathRecognizer extends NumberWithUnitRecognizer {
    public BedBathRecognizer() {
        this(null, NumberWithUnitOptions.None, true);
    }

    public BedBathRecognizer(String culture) {
        this(culture, NumberWithUnitOptions.None, false);
    }

    public BedBathRecognizer(NumberWithUnitOptions options) {
        this(null, options, true);
    }

    public BedBathRecognizer(NumberWithUnitOptions options, boolean lazyInitialization) {
        this(null, options, lazyInitialization);
    }

    public BedBathRecognizer(String culture, NumberWithUnitOptions options, boolean lazyInitialization) {
        super(culture, options, lazyInitialization);
    }

    public BedBathModel getBedBathModel(String culture, boolean fallbackToDefaultCulture) {
        return getModel(BedBathModel.class, culture, fallbackToDefaultCulture);
    }

    public static List<ModelResult> recognizeBedBath(String query, String culture) {
        return recognizeBedBath(query, culture, NumberWithUnitOptions.None);
    }

    public static List<ModelResult> recognizeBedBath(String query, String culture, NumberWithUnitOptions options) {
        return recognizeBedBath(query, culture, options, true);
    }

    public static List<ModelResult> recognizeBedBath(String query, String culture, NumberWithUnitOptions options, boolean fallbackToDefaultCulture) {
        BedBathRecognizer recognizer = new BedBathRecognizer(options);
        IModel model = recognizer.getBedBathModel(culture, fallbackToDefaultCulture);
        return model.parse(query);
    }

    @Override
    protected void initializeConfiguration() {
        super.initializeConfiguration();

        registerModel(BedBathModel.class, Culture.English, (options) ->
                new BedBathModel(ImmutableMap.of(
                        new NumberWithUnitExtractor(new BedBathExtractorConfiguration()),
                        new NumberWithUnitParser(new BedBathParserConfiguration())
                ))
        );
    }
}
