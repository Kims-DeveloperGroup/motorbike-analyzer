package com.devoo.motorbike.analyzer.processor.naver;

import com.devoo.motorbike.analyzer.domain.NaverDocumentWrapper;
import com.devoo.motorbike.analyzer.processor.Processor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Component;

@Component
public class NaverCafeDocumentRefiner implements Processor<NaverDocumentWrapper, NaverDocumentWrapper> {
    private static final String NAVER_CAFE_BASE_DOMAIN_URL = "http://cafe.naver.com";

    @Override
    public NaverDocumentWrapper process(NaverDocumentWrapper param) {
        param.setDocument(refineDocumentToPureContent(param.getDocument()));
        return param;
    }

    @Override
    public boolean test(NaverDocumentWrapper param) {
        return param.getTargetNaverItem().getLink().startsWith(NAVER_CAFE_BASE_DOMAIN_URL);
    }

    private Document refineDocumentToPureContent(Document document) {
        document.getElementsByTag("script").forEach(Node::remove);
        Element inbox = document.getElementsByClass("inbox").first();
        if (inbox == null) {
            return document;
        }
        return Jsoup.parse(inbox.toString());
    }
}