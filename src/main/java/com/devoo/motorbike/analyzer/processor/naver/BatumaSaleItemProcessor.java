package com.devoo.motorbike.analyzer.processor.naver;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.processor.Processor;
import com.devoo.motorbike.analyzer.processor.parser.YearParser;
import com.google.gson.JsonElement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Slf4j
public class BatumaSaleItemProcessor implements Processor<NaverDocumentWrapper, JsonElement> {
    public static final String BATUMA_BASE_DOMAIN_URL = "http://cafe.naver.com/bikecargogo";
    private final Pattern modelInfoPattern = Pattern.compile(".*모델.*");
    private final Pattern RELEASED_YEAR_TEXT_PATTERN = Pattern.compile(".*연식.*");
    private final YearParser yearParser;

    @Autowired
    public BatumaSaleItemProcessor(YearParser yearParser) {
        this.yearParser = yearParser;
    }

    @Override
    public JsonElement process(NaverDocumentWrapper param) {
        BatumaSaleItem batumaSaleItem = new BatumaSaleItem();
        Document rawDocument = param.getDocument();

        batumaSaleItem.setSaleStatus(extractSaleStatus(rawDocument));
        batumaSaleItem.setPrice(extractPrice(rawDocument));
        batumaSaleItem.setModel(extractModel(rawDocument));
        batumaSaleItem.setReleaseYear(extractReleasedYear(rawDocument));
        log.debug("Document is processed to parse to BatumaSaleItem. {}", param.getTargetNaverItem().getLink());
        return gson.toJsonTree(batumaSaleItem);
    }

    private Integer extractReleasedYear(Document rawDocument) {
        Optional<Elements> releaseYear = Optional.ofNullable(rawDocument.getElementsMatchingOwnText(RELEASED_YEAR_TEXT_PATTERN));
        if (releaseYear.isPresent()) {
            String text = releaseYear.get().get(0).text();
            Integer releasedYear = yearParser.getYear(text);
            if (releasedYear != null) {
                return releasedYear;
            }
        }
        return null;
    }

    private Double extractPrice(Document rawDocument) {
        Optional<Elements> price = Optional.ofNullable(rawDocument.select(".cost"));
        if (price.isPresent()) {
            String text = price.get().get(0).text();
            text = text.replace("원", "");
            text = text.replace(",", "");
            return Double.valueOf(text);
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
        Optional<Elements> model = Optional.ofNullable(rawDocument.getElementsMatchingOwnText(modelInfoPattern));
        if (model.isPresent()) {
            return model.get().get(0).text();
        }
        return null;
    }

    @Override
    public boolean test(NaverDocumentWrapper candidateItemToProcess) {
        return candidateItemToProcess.getTargetNaverItem().getLink().startsWith(BATUMA_BASE_DOMAIN_URL);
    }

    @Data
    private class BatumaSaleItem {
        private final String ITEM_TYPE = "BATUMA_SALE_ITEM";
        private String model;
        private Integer displacement;
        private Integer releaseYear;
        private Double price;
        private String region;
        private String seller;
        private SaleStatus saleStatus;
    }
}
