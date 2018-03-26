package com.devoo.motorbike.analyzer.parser;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.domain.SaleItem;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Locale;

@Component
@Slf4j
public class NaverDocumentParser {
    DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR).appendLiteral('.')
            .appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral('.')
            .appendValue(ChronoField.DAY_OF_MONTH).appendLiteral('.')
            .toFormatter(Locale.KOREA);

    public SaleItem parseToSaleItem(NaverDocumentWrapper documentWrapper) {
        SaleItem saleItem = new SaleItem();
        saleItem.setId(documentWrapper.getNaverItem().getLink());
        String refinedDocument = refineDocumentToPureContent(documentWrapper.getDocument());
        saleItem.setRawDocument(refinedDocument);
        saleItem.setOnlineShop(documentWrapper.getNaverItem().getCafeName());
        saleItem.setUrl(documentWrapper.getNaverItem().getLink());
        saleItem.setTitle(documentWrapper.getNaverItem().getTitle());
        log.debug("Naver document parsed to result item: {}", documentWrapper.getNaverItem().getLink());
        try {
            saleItem.setUpdatedDate(LocalDate.parse(documentWrapper.getNaverItem().getDate(), dateTimeFormatter));
        } catch (DateTimeParseException e) {
            log.debug("Exception: {}, {}", e, documentWrapper.getNaverItem().getDate());
        }
        return saleItem;
    }

    private String refineDocumentToPureContent(Document document) {
        document.getElementsByTag("script").forEach(element -> element.remove());
        Element inbox = document.getElementsByClass("inbox").first();
        if (inbox == null) {
            return null;
        }
        return inbox.toString();
    }
}