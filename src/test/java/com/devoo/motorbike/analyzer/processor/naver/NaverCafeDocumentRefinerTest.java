package com.devoo.motorbike.analyzer.processor.naver;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import org.assertj.core.api.Assertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class NaverCafeDocumentRefinerTest {
    private static final NaverCafeDocumentRefiner refiner = new NaverCafeDocumentRefiner();
    private static Logger log = LoggerFactory.getLogger(NaverCafeDocumentRefinerTest.class);
    private static Document sampleDoc;

    @BeforeClass
    public static void readSampleHtmlDoc() throws IOException {
        String resource = NaverCafeDocumentRefinerTest.class.
                getClassLoader().getResource("naverCafeItemDocSample.html").getFile();
        sampleDoc = Jsoup.parse(new File(resource), "utf-8");
    }

    @Test
    public void shouldProcessDocumentToContainOnlyContentArea_whenDocumentIsRawFormat() {
        //Given
        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, new TargetNaverItem());
        int docLengthBeforeProcessed = documentWrapper.getDocument().toString().length();
        int docLengthAfterProcessed;

        //When
        NaverDocumentWrapper processed = refiner.process(documentWrapper);
        docLengthAfterProcessed = processed.getDocument().toString().length();

        //Then
        Assertions.assertThat(docLengthAfterProcessed).isLessThan(docLengthBeforeProcessed);
        log.debug("before length: {}  after length: {}, reduced by {}",
                docLengthBeforeProcessed, docLengthAfterProcessed, docLengthAfterProcessed - docLengthBeforeProcessed);
    }

}