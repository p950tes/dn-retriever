package p950tes.dnretriever.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import p950tes.dnretriever.model.ResultCard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResultCardParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultCardParser.class);

    public List<ResultCard> parseCards(Document document) {
        Elements resultCards = document.select(".result__card");

        List<ResultCard> resultCardList = new ArrayList<>();

        LOGGER.debug("Cards to parse: " + resultCards.size());

        for (Element resultCardElement : resultCards) {
            try {
                resultCardList.add(parse(resultCardElement));
            } catch (Exception e) {
                LOGGER.debug("Failed to parse resultCard " + resultCardElement, e);
            }
        }
        return resultCardList;
    }

    private ResultCard parse(Element resultCardElement) {
        LOGGER.debug("Parsing resultCard {}", resultCardElement);
        ResultCard resultCard = new ResultCard();
        resultCard.setImageUrl(resolveImageUrl(resultCardElement));
        resultCard.setDate(resolveDate(resultCardElement));
        resultCard.setDescription(resolveDescription(resultCardElement));
        resultCard.setPageNumber(resolvePageNumber(resultCardElement));
        resultCard.setIssueNumber(resolveIssueNumber(resultCardElement));
        return resultCard;
    }

    private String resolveImageUrl(Element resultCardElement) {
        Element imageLinkDiv = resultCardElement.select(".result__card-image-nojs").first();
        Elements linkElements = imageLinkDiv.getElementsByTag("a");

        if (linkElements.isEmpty()) {
            LOGGER.debug("Failed to resolve Image resultCardElement: " + resultCardElement);
            return null;
        }
        return linkElements.first().attr("href");
    }
    private LocalDate resolveDate(Element resultCardElement) {
        Element cardImageDiv = resultCardElement.select(".lazy-image").first();
        String dateString = cardImageDiv.attr("data-paper-date");
        return LocalDate.parse(dateString);
    }
    private Integer resolvePageNumber(Element resultCardElement) {
        Element cardImageDiv = resultCardElement.select(".lazy-image").first();
        String pageNumberString = cardImageDiv.attr("data-paper-page-no");
        return Integer.parseInt(pageNumberString);
    }
    private String resolveIssueNumber(Element resultCardElement) {
        Element cardImageDiv = resultCardElement.select(".lazy-image").first();
        return cardImageDiv.attr("data-paper-issue-no");
    }
    private static String resolveDescription(Element resultCardElement) {
        Element textDiv = resultCardElement.select(".result__card-text").first();
        return textDiv.text();
    }


}
