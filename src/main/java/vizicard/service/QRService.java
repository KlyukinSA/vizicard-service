package vizicard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QRService {

	public String generate(String shortname) throws IOException, InterruptedException {
		String body = getFileText("qrcode-monkey-request.json");
		body = body.replace("$1", shortname);
		System.out.println(body);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.qrcode-monkey.com/qr/custom"))
				.header("content-type", "application/json")
				.method("POST", HttpRequest.BodyPublishers.ofString(body))
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
		return response.body();
	}

	private String getFileText(String fileName) {
		try {
			InputStream is = new ClassPathResource(fileName).getInputStream();
			return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
					.lines()
					.collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
