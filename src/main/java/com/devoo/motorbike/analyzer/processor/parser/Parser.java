package com.devoo.motorbike.analyzer.processor.parser;

public interface Parser<T> {
    T parse(String text);
}