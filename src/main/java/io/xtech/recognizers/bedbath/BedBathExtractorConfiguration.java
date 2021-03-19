package io.xtech.recognizers.bedbath;

import com.google.common.collect.ImmutableMap;
import com.microsoft.recognizers.text.Culture;
import com.microsoft.recognizers.text.CultureInfo;
import com.microsoft.recognizers.text.numberwithunit.english.extractors.EnglishNumberWithUnitExtractorConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BedBathExtractorConfiguration extends EnglishNumberWithUnitExtractorConfiguration {
    public BedBathExtractorConfiguration() {
        this(new CultureInfo(Culture.English));
    }

    public BedBathExtractorConfiguration(CultureInfo ci) {
        super(ci);
    }

    @Override
    public String getExtractType() {
        return BedBathModel.MODEL_TYPE;
    }

    @Override
    public Map<String, String> getSuffixList() {
        return BedBathSuffixList;
    }

    @Override
    public Map<String, String> getPrefixList() {
        return Collections.emptyMap();
    }

    @Override
    public List<String> getAmbiguousUnitList() {
        return Collections.emptyList();
    }

    public static Map<String, String> BedBathSuffixList = new ImmutableMap.Builder<String, String>()
            .put("Bedroom", "-bedroom|bedroom|bedrooms|-bdroom|bdroom|bdrooms|bed room|bed rooms|bd room|bd rooms|-bed|bed|beds|-bd|bd|bds")
            .put("Bathroom", "-bathroom|bathroom|bathrooms|bath room|bath rooms|restroom|restrooms|shower|showers|shower room|shower rooms|-bath|bath|baths")
            .build();
}
