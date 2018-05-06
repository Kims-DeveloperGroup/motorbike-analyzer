package com.devoo.motorbike.analyzer.processor.naver;

import com.devoo.motorbike.analyzer.domain.Model;
import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.ProcessResult;
import com.devoo.motorbike.analyzer.processor.Processor;
import com.devoo.motorbike.analyzer.processor.parser.ModelNameParser;
import com.devoo.motorbike.analyzer.processor.parser.NumericValueParser;
import com.devoo.motorbike.analyzer.processor.parser.YearParser;
import com.devoo.motorbike.analyzer.service.ProductModelInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.devoo.motorbike.analyzer.processor.parser.ModelNameParser.UNDEFINED_MODEL;

@Component
@Slf4j
public class BatumaSaleItemProcessor implements Processor<NaverDocumentWrapper, BatumaSaleItemProcessor.BatumaSaleItem> {
    public static final String BATUMA_BASE_DOMAIN_URL = "http://cafe.naver.com/bikecargogo";
    private final Pattern MODEL_TEXT_PATTERN = Pattern.compile(".+모델.+");
    private final Pattern RELEASED_YEAR_TEXT_PATTERN = Pattern.compile(".+연식.+");
    private Pattern MILEAGE_TEXT_PATTERN = Pattern.compile(".+적산거리.+");
    private final YearParser yearParser;
    private final NumericValueParser numericValueParser;
    private final ModelNameParser modelNameParser;
    private final ProductModelInfoService productModelInfoService;

    @Autowired
    public BatumaSaleItemProcessor(YearParser yearParser, NumericValueParser numericValueParser, ModelNameParser modelNameParser, ProductModelInfoService productModelInfoService) {
        this.yearParser = yearParser;
        this.numericValueParser = numericValueParser;
        this.modelNameParser = modelNameParser;
        this.productModelInfoService = productModelInfoService;
    }

    @Override
    public BatumaSaleItem process(NaverDocumentWrapper param) {
        BatumaSaleItem batumaSaleItem = new BatumaSaleItem();
        Document rawDocument = param.getDocument();

        batumaSaleItem.setSaleStatus(extractSaleStatus(rawDocument));
        batumaSaleItem.setPrice(extractPrice(rawDocument));
        batumaSaleItem.setModel(extractModel(rawDocument));
        batumaSaleItem.setReleaseYear(extractReleasedYear(rawDocument));
        batumaSaleItem.setMileage(extractMileage(rawDocument));
        log.debug("Document is processed to parse to BatumaSaleItem. {}", param.getTargetNaverItem().getLink());
        return batumaSaleItem;
    }

    private Long extractMileage(Document rawDocument) {
        String mileageInfoText = getFirstElementMatchingOwnTextFromDocument(rawDocument, MILEAGE_TEXT_PATTERN);
        return numericValueParser.parse(mileageInfoText);
    }

    private Integer extractReleasedYear(Document rawDocument) {
        String releaseYearInfoText = getFirstElementMatchingOwnTextFromDocument(rawDocument, RELEASED_YEAR_TEXT_PATTERN);

        if (releaseYearInfoText.isEmpty()) {
            return null;
        }
        return yearParser.parse(releaseYearInfoText);
    }

    private Long extractPrice(Document rawDocument) {
        Optional<Elements> price = Optional.ofNullable(rawDocument.select(".cost"));
        if (price.isPresent()) {
            String text = price.get().get(0).text();
            return numericValueParser.parse(text);
        }
        return null;
    }

    private SaleStatus extractSaleStatus(Document rawDocument) {
        Optional<Elements> onSaleFlag = Optional.ofNullable(rawDocument.select(".details .sale_now"));
        if (onSaleFlag.isPresent()) {
            return SaleStatus.ON_SALE;
        } else if (Optional.of(rawDocument.select(".details .sale_complete")).isPresent()) {
            return SaleStatus.SOLD;
        } else {
            return SaleStatus.UNDEFINED;
        }
    }

    private String extractModel(Document rawDocument) {
        String modelInfoText = getFirstElementMatchingOwnTextFromDocument(rawDocument, MODEL_TEXT_PATTERN);
        if (modelInfoText.isEmpty()) {
            return null;
        }
        String parsedModelName = modelNameParser.parse(modelInfoText);
        if (parsedModelName.equals(UNDEFINED_MODEL)) {
            return modelInfoText;
        }
        Optional<Model> matchedModelByName = productModelInfoService.findMatchedModelByName(parsedModelName);
        if (matchedModelByName.isPresent()) {
            return matchedModelByName.get().getName();
        } else {
            return parsedModelName;
        }
    }

    private String getFirstElementMatchingOwnTextFromDocument(Document rawDocument, Pattern textPattern) {
        Elements elements = rawDocument.getElementsMatchingOwnText(textPattern);
        if (elements == null || elements.isEmpty()) {
            return "";
        }
        return elements.get(0).text();
    }

    @Override
    public boolean test(NaverDocumentWrapper candidateItemToProcess) {
        return candidateItemToProcess.getTargetNaverItem().getLink().startsWith(BATUMA_BASE_DOMAIN_URL);
    }

    @Data
    public static class BatumaSaleItem extends ProcessResult {
        private final String ITEM_TYPE = "BATUMA_SALE_ITEM";
        private String model;
        private Integer displacement;
        private Integer releaseYear;
        private Long price;
        private Long mileage;
        private String region;
        private String seller;
        private SaleStatus saleStatus;
    }
}
