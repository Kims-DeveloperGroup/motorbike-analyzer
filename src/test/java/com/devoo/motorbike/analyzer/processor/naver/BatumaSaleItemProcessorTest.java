package com.devoo.motorbike.analyzer.processor.naver;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.naver.TargetNaverItem;
import com.devoo.motorbike.analyzer.processor.parser.ModelNameParser;
import com.devoo.motorbike.analyzer.processor.parser.NumericValueParser;
import com.devoo.motorbike.analyzer.processor.parser.YearParser;
import com.devoo.motorbike.analyzer.service.ProductModelInfoService;
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
        BatumaSaleItemProcessor.BatumaSaleItem processed = batumaSaleItemProcessor.execute(documentWrapper);

        //Then
        assertThat(processed.getSaleStatus())
                .isEqualTo(SaleStatus.ON_SALE);
    }

    @Test
    public void shouldPriceBeRExtractedFromDocument_whenTheDocHasPriceInfo() {
        //Given
        TargetNaverItem naverItem = new TargetNaverItem();
        naverItem.setLink(BATUMA_BASE_DOMAIN_URL);

        NaverDocumentWrapper documentWrapper = new NaverDocumentWrapper(sampleDoc, naverItem);
        //When
        BatumaSaleItemProcessor.BatumaSaleItem processed = batumaSaleItemProcessor.execute(documentWrapper);

        //Then
        Long actualPriceFromDoc = processed.getPrice();
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
        BatumaSaleItemProcessor.BatumaSaleItem processed = batumaSaleItemProcessor.execute(documentWrapper);

        //Then
        String actualModel = processed.getModel();
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
        BatumaSaleItemProcessor.BatumaSaleItem processed = batumaSaleItemProcessor.execute(documentWrapper);

        //Then
        int actualReleasedYear = processed.getReleaseYear();
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
        BatumaSaleItemProcessor.BatumaSaleItem processed = batumaSaleItemProcessor.execute(documentWrapper);

        //Then
        long actualReleasedYear = processed.getMileage();
        assertThat(actualReleasedYear).isEqualTo(expectedMileage);
    }
}