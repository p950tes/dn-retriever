package p950tes.dnretriever.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@ConfigurationProperties("dnretriever")
public class DnRetrieverProperties {

    private String outputDirectory;
    private String outputFieldDelimiter;

    private String query;
    private Integer numberOfYearsPerSearch;
    private LocalDate startDate;
    private LocalDate endDate;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getOutputFieldDelimiter() {
        return outputFieldDelimiter;
    }

    public void setOutputFieldDelimiter(String outputFieldDelimiter) {
        this.outputFieldDelimiter = outputFieldDelimiter;
    }

    public Integer getNumberOfYearsPerSearch() {
        return numberOfYearsPerSearch;
    }

    public void setNumberOfYearsPerSearch(Integer numberOfYearsPerSearch) {
        this.numberOfYearsPerSearch = numberOfYearsPerSearch;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
