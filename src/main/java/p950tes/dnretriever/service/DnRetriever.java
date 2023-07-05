package p950tes.dnretriever.service;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import p950tes.dnretriever.adapter.DnClient;
import p950tes.dnretriever.model.ResultCard;
import p950tes.dnretriever.properties.DnRetrieverProperties;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class DnRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(DnRetriever.class);

	private static final String BASE_URL = "https://arkivet.dn.se/sok";

	private final DnClient dnClient;
	private final DnRetrieverProperties properties;
	private final ResultCardParser resultCardParser = new ResultCardParser();

	private File destinationFile;
	private long pagesRetrieved = 0;
	private long resultsRetrieved = 0;

	@Autowired
	public DnRetriever(DnClient dnClient, DnRetrieverProperties properties) {
		this.dnClient = dnClient;
		this.properties = properties;
	}

	public void run() throws Exception {

		destinationFile = createDestinationFile();

		System.out.println();
		LOGGER.info("Query: {}", properties.getQuery());
		LOGGER.info("Start date: {}", properties.getStartDate());
		LOGGER.info("End date: {}", properties.getEndDate());
		System.out.println();
		
		LocalDate startDate = properties.getStartDate();
		LocalDate endDate = startDate.plusYears(properties.getNumberOfYearsPerSearch()).with(TemporalAdjusters.lastDayOfYear());

		while (startDate.isBefore(properties.getEndDate())) {
			process(startDate, endDate);

			startDate = endDate.plusDays(1);
			endDate = startDate.plusYears(properties.getNumberOfYearsPerSearch()).with(TemporalAdjusters.lastDayOfYear());
			if (endDate.isAfter(properties.getEndDate())) {
				endDate = properties.getEndDate();
			}
		}

		System.out.println();
		LOGGER.info("--- PARSING COMPLETE ---");
		LOGGER.info("Pages parsed: " + pagesRetrieved);
		LOGGER.info("Results retrieved: " + resultsRetrieved);
		LOGGER.info("Results stored in: {}", destinationFile);
		LOGGER.info("Finished successfully");
	}

	private int resolveTotalResults(Document document) {
		Elements resultsContainer = document.select(".result__container");
		String totalHitsString = resultsContainer.attr("data-total-hits");
		return Integer.parseInt(totalHitsString);
	}

	private File createDestinationFile() {
		String outputFilePath = properties.getOutputDirectory() + "/dn-retriever-results." + System.currentTimeMillis() + ".csv";
		try {
			LOGGER.info("Creating results file: {}", outputFilePath);
			File destinationFile = new File(outputFilePath);
			destinationFile.createNewFile();

			String header = String.join(properties.getOutputFieldDelimiter(), "Date", "IssueNumber", "PageNumber", "Description", "ImageUrl");
			FileUtils.writeLines(destinationFile, List.of(header), false);
			return destinationFile;
		} catch (Exception e) {
			throw new RuntimeException("Failed to create destination file: " + outputFilePath, e);
		}
	}

	private void process(LocalDate startDate, LocalDate endDate) throws Exception {

		String startDateString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
		String endDateString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

		LOGGER.info("Processing {} - {}", startDateString, endDateString);

		String nextLink = String.format("%s?q=%s&from=%s&to=%s", BASE_URL, properties.getQuery(), startDateString, endDateString);

		long localPagesProcessed = 0;

		while (nextLink != null) {

			String body = dnClient.fetch(nextLink);

			Document document = Jsoup.parse(body);

			long results = resolveTotalResults(document);
			if (results == 0) {
				LOGGER.info("No results {} - {}", startDateString, endDateString);
				return;
			}
			if (localPagesProcessed == 0) {
				LOGGER.info("Results: {}", results);
			}

			List<ResultCard> resultCards = resultCardParser.parseCards(document);

			localPagesProcessed++;
			pagesRetrieved++;
			resultsRetrieved += resultCards.size();
			saveResults(resultCards);
			nextLink = resolveNextLink(document);
		}
		LOGGER.info("Pages traversed: {}, Results parsed: {}", pagesRetrieved, resultsRetrieved);
	}

	private String resolveNextLink(Document document) {
		Elements nextLinks = document.select(".pagination-button--next");
		if (nextLinks.isEmpty()) {
			return null;
		}
		String href = nextLinks.first().attr("href");
		return BASE_URL + href;
	}

	private void saveResults(List<ResultCard> resultCards) throws IOException {
		try {
			List<String> lines = resultCards.stream().map(this::toTextFormat).toList();
			FileUtils.writeLines(destinationFile, lines, true);
		} catch (Exception e) {
			LOGGER.error("Failed to store results to file", e);
			throw e;
		}
	}
	private String toTextFormat(ResultCard resultCard) {
		String delimiter = properties.getOutputFieldDelimiter();

		return String.join(delimiter,
				Objects.toString(resultCard.getDate(), ""),
				Objects.toString(resultCard.getIssueNumber(), ""),
				Objects.toString(resultCard.getPageNumber(), ""),
				Objects.toString(resultCard.getDescription(), "").replaceAll(Pattern.quote(delimiter), ";"),
				Objects.toString(resultCard.getImageUrl(), ""));
	}
}
