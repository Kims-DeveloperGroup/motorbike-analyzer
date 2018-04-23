package com.devoo.motorbike.analyzer.processor.naver;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.motorbike.analyzer.processor.parser.YearParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.devoo.motorbike.analyzer.processor.naver.BatumaSaleItemProcessor.BATUMA_BASE_DOMAIN_URL;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class BatumaSaleItemProcessorTest {
    private static final BatumaSaleItemProcessor batumaSaleItemProcessor = new BatumaSaleItemProcessor(new YearParser());
    private static Document sampleDoc;

    @BeforeClass
    public static void readSampleHtmlDoc() throws IOException {
        String resource = NaverCafeDocumentRefinerTest.class.
                getClassLoader().getResource("batumaSaleItemSample.html").getFile();
        sampleDoc = Jsoup.parse(new File(resource), "utf-8");
    }

    @Test
    public void shouldBeSaleItemStatusIsExtractedFromDocument_whenTheDocHasOnSaleStatus() {
        //Given
        TargetNaverItem naverItem = new TargetNaverItem();
        naverItem.setLink(BATUMA_BASE_DOMAIN_URL);

        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, naverItem);

        //When
        JsonObject processed = batumaSaleItemProcessor.execute(documentWrapper).getAsJsonObject();

        //Then
        assertThat(processed.get("saleStatus").getAsString())
                .isEqualTo(SaleStatus.ON_SALE.toString());
    }

    @Test
    public void shouldPriceBeRExtractedFromDocument_whenTheDocHasPriceInfo() {
        //Given
        TargetNaverItem naverItem = new TargetNaverItem();
        naverItem.setLink(BATUMA_BASE_DOMAIN_URL);

        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, naverItem);
        //When
        JsonObject processed = batumaSaleItemProcessor.execute(documentWrapper).getAsJsonObject();

        //Then
        JsonElement actualPriceFromDoc = processed.get("price");
        assertThat(actualPriceFromDoc).isNotNull();
    }

    @Test
    public void shouldBeModelExtractedFromDocument_whenTheDocHasModelName() {
        //Given
        TargetNaverItem naverItem = new TargetNaverItem();
        naverItem.setLink(BATUMA_BASE_DOMAIN_URL);
        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, naverItem);
        String expectedModelText = "1. 제작사, 모델명 : BMW K1300S";

        //When
        JsonObject processed = batumaSaleItemProcessor.execute(documentWrapper).getAsJsonObject();

        //Then
        String actualModel = processed.get("model").getAsString();
        assertThat(actualModel).isEqualTo(expectedModelText);
    }

    @Test
    public void shouldBeReleasedYearExtractedFromDocument_whenTheDocHas() {
        //Given
        TargetNaverItem naverItem = new TargetNaverItem();
        naverItem.setLink(BATUMA_BASE_DOMAIN_URL);
        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, naverItem);
        int expectedReleaseYear = 2010;

        //When
        JsonObject processed = batumaSaleItemProcessor.execute(documentWrapper).getAsJsonObject();

        //Then
        int actualReleasedYear = processed.get("releaseYear").getAsInt();
        assertThat(actualReleasedYear).isEqualTo(expectedReleaseYear);
    }
}