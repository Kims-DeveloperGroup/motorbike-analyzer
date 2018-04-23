package com.devoo.motorbike.analyzer.processor;

import com.google.gson.Gson;

public interface Processor<T, R> {
    Gson gson = new Gson();

    default R execute(T param) {
        if (test(param)) {
            return process(param);
        }
        return null;
    }

    /**
     * Processes a given item
     *
     * @param param target item to process
     * @return the processed item
     */
    R process(T param);

    /**
     * test a given item whether the item is appropriate to be processed.
     * If appropriate, the processor runs process(item)
     * @param param
     * @return true if the given item is the right target item.
     */
    boolean test(T param);
}
