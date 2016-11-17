package net.smolok.lib.machinelearning.tag

interface TagService {

    void train(Map<String, List<String>> taggedTexts)

    List<TagPredicition> predict(Map<String, String> texts)

}