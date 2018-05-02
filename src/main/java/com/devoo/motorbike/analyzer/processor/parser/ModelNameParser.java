package com.devoo.motorbike.analyzer.processor.parser;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ModelNameParser implements Parser<String> {
    private static final Pattern modelNamePattern1_GSX_R_600 = Pattern.compile("([A-z]+.[A-z]+.[0-9]+)");
    private static final Pattern modelNamePattern2_CBR_600_RR = Pattern.compile("([A-z]+.[0-9]+.[A-z]+)");
    private static final Pattern modelNamePattern3_600_RR = Pattern.compile("([0-9]+.[A-z]+)");
    private static final Pattern[] modelNamePatterns = {modelNamePattern1_GSX_R_600, modelNamePattern2_CBR_600_RR, modelNamePattern3_600_RR};
    private final String UNDEFINED_MODEL = "UNDEFINED";

    @Override
    public String parse(String text) {
        Map<String, AtomicInteger> matchedModelsWithCount = getModelNamesWithMatchedCount(text);
        String mostMatchedModelName = getMostMatchedModelName(matchedModelsWithCount);
        return mostMatchedModelName;
    }

    private String getMostMatchedModelName(Map<String, AtomicInteger> matchedModelsWithCount) {
        String mostMatchedModel = UNDEFINED_MODEL;
        int mostCount = 0;
        String[] modelNames = matchedModelsWithCount.keySet().toArray(new String[]{});
        for (String name : modelNames) {
            int matchedCount = matchedModelsWithCount.get(name).get();
            if (matchedCount > mostCount) {
                mostMatchedModel = name;
                mostCount = matchedCount;
            }
        }
        return mostMatchedModel;
    }

    private Map<String, AtomicInteger> getModelNamesWithMatchedCount(String text) {
        Map<String, AtomicInteger> matchedModelsWithCount = new HashMap<>();

        for (Pattern pattern : modelNamePatterns) {
            Matcher modelNameMatcher = pattern.matcher(text);
            while (modelNameMatcher.find()) {
                incrementCountForMatchedName(matchedModelsWithCount, modelNameMatcher);
            }
        }
        return matchedModelsWithCount;
    }

    private void incrementCountForMatchedName(Map<String, AtomicInteger> matchedModelsWithCount, Matcher modelNameMatcher) {
        String matched = modelNameMatcher.group();
        if (matchedModelsWithCount.containsKey(matched)) {
            matchedModelsWithCount.compute(matched, (matchedKey, matchedCount) -> {
                matchedCount.incrementAndGet();
                return matchedCount;
            });
        } else {
            matchedModelsWithCount.put(matched, new AtomicInteger(1));
        }
    }
}