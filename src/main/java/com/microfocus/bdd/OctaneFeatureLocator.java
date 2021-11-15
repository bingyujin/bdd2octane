package com.microfocus.bdd;

import com.microfocus.bdd.api.OctaneFeature;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OctaneFeatureLocator {

    private FeatureFileLocator featureFileLocator;
    private Map<String, OctaneFeature> featureFileToFeatureMap = new HashMap();

    public OctaneFeatureLocator(List<String> featureFiles) {
        featureFileLocator = new FeatureFileLocator(featureFiles);
    }

    public Optional<OctaneFeature> getOctaneFeatureByName(String featureName) throws IOException {
        FeatureFileMeta featureFileMeta = featureFileLocator.getFeatureFileMeta(featureName);
        if (null == featureFileMeta) {
            return Optional.empty();
        }
        return Optional.of(createOctaneFeatureIfNotCached(featureFileMeta));
    }

    public OctaneFeature getOctaneFeatureByPath(String featureFile) {
        FeatureFileMeta featureFileMeta = featureFileLocator.createFeatureFileMeta(featureFile);
        return createOctaneFeatureIfNotCached(featureFileMeta);
    }

    public Optional<OctaneFeature> getFeatureByScenarioName(String scenarioName) {
        Optional<FeatureFileMeta> meta = featureFileLocator.getFeatureFileMetaByScenarioNameFromCache(scenarioName);
        if (meta.isPresent()) {
            return Optional.of(createOctaneFeatureIfNotCached(meta.get()));
        }
        Optional<OctaneFeature> featureOpt = featureFileLocator.getOctaneFeatureByScenarioNameFromFile(scenarioName);
        if (featureOpt.isPresent()) {
            OctaneFeature octaneFeature = featureOpt.get();
            String featureFile = octaneFeature.getFeatureFile();
            return Optional.of(featureFileToFeatureMap.computeIfAbsent(featureFile, key -> octaneFeature));
        }
        return Optional.empty();
    }

    public void remove(OctaneFeature octaneFeature) {
        featureFileToFeatureMap.remove(octaneFeature.getFeatureFile());
    }

    private OctaneFeature createOctaneFeatureIfNotCached(FeatureFileMeta featureFileMeta) {
        return featureFileToFeatureMap.computeIfAbsent(featureFileMeta.getFeatureFile(),
                key -> featureFileLocator.getOctaneFeature(featureFileMeta));
    }
}
