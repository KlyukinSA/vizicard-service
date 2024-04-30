package vizicard.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vizicard.exception.CustomException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QRService {

	@Value("${front-url-base}")
	private String urlBase;

	public String generate(String shortname) throws IOException, InterruptedException {
		String body = getFileText("qrcode-monkey-request.json");
		body = body.replace("$1", urlBase);
		body = body.replace("$2", shortname);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.qrcode-monkey.com/qr/custom"))
				.header("content-type", "application/json")
				.method("POST", HttpRequest.BodyPublishers.ofString(body))
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		String body1 = response.body();
		Map<String, Object> jsonElements =  new ObjectMapper().readValue(body1, new TypeReference<>() {});
		String url = (String) jsonElements.get("imageUrl");
		if (url == null) {
			throw new CustomException(body1, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return url.substring(2);
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
