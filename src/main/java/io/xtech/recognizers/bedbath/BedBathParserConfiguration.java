package io.xtech.recognizers.bedbath;

import com.microsoft.recognizers.text.Culture;
import com.microsoft.recognizers.text.CultureInfo;
import com.microsoft.recognizers.text.numberwithunit.english.parsers.EnglishNumberWithUnitParserConfiguration;

public class BedBathParserConfiguration extends EnglishNumberWithUnitParserConfiguration {

    public BedBathParserConfiguration() {
        this(new CultureInfo(Culture.English));
    }

    public BedBathParserConfiguration(CultureInfo cultureInfo) {
        super(cultureInfo);

        this.bindDictionary(BedBathExtractorConfiguration.BedBathSuffixList);
    }
}
