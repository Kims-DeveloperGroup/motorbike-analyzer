package com.devoo.motorbike.analyzer.processor.naver;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.motorbike.analyzer.processor.parser.ModelNameParser;
import com.devoo.motorbike.analyzer.processor.parser.NumericValueParser;
import com.devoo.motorbike.analyzer.processor.parser.YearParser;
import com.devoo.motorbike.analyzer.service.ProductModelInfoService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static com.devoo.motorbike.analyzer.processor.naver.BatumaSaleItemProcessor.BATUMA_BASE_DOMAIN_URL;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BatumaSaleItemProcessorTest {
    @Mock
    private ProductModelInfoService productModelInfoService;

    @Mock
    private YearParser yearParser;

    @Mock
    private NumericValueParser numericValueParser;

    @Mock
    private ModelNameParser modelNameParser;

    @InjectMocks
    private BatumaSaleItemProcessor batumaSaleItemProcessor;

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
        String modelName = "K1300S";
        when(modelNameParser.parse(anyString())).thenReturn(modelName);
        when(productModelInfoService.findMatchedModelByName(modelName)).thenReturn(Optional.empty());
        //When
        JsonObject processed = batumaSaleItemProcessor.execute(documentWrapper).getAsJsonObject();

        //Then
        String actualModel = processed.get("model").getAsString();
        assertThat(actualModel).isNotNull();
    }

    @Test
    public void shouldBeReleasedYearExtractedFromDocument_whenTheDocHas() {
        //Given
        TargetNaverItem naverItem = new TargetNaverItem();
        naverItem.setLink(BATUMA_BASE_DOMAIN_URL);
        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, naverItem);
        int expectedReleaseYear = 2010;
        when(yearParser.parse(anyString())).thenReturn(expectedReleaseYear);

        //When
        JsonObject processed = batumaSaleItemProcessor.execute(documentWrapper).getAsJsonObject();

        //Then
        int actualReleasedYear = processed.get("releaseYear").getAsInt();
        assertThat(actualReleasedYear).isEqualTo(expectedReleaseYear);
    }

    @Test
    public void shouldBeMileageExtractedFromDocument_whenTheDocHas() {
        //Given
        TargetNaverItem naverItem = new TargetNaverItem();
        naverItem.setLink(BATUMA_BASE_DOMAIN_URL);
        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, naverItem);
        long expectedMileage = 7486;
        when(numericValueParser.parse(anyString())).thenReturn(expectedMileage);

        //When
        JsonObject processed = batumaSaleItemProcessor.execute(documentWrapper).getAsJsonObject();

        //Then
        long actualReleasedYear = processed.get("mileage").getAsLong();
        assertThat(actualReleasedYear).isEqualTo(expectedMileage);
    }
}