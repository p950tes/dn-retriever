package p950tes.dnretriever;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import p950tes.dnretriever.service.DnRetriever;

@SpringBootApplication
public class DnRetrieverApplication implements CommandLineRunner {

	private final DnRetriever dnRetriever;

	@Autowired
	public DnRetrieverApplication(DnRetriever dnRetriever) {
		this.dnRetriever = dnRetriever;
	}

	public static void main(String[] args) {
		SpringApplication.run(DnRetrieverApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		dnRetriever.run();
	}
}
