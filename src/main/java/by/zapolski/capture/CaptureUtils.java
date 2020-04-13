package by.zapolski.capture;

import by.zapolski.capture.model.dto.SentenceInfo;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CaptureUtils {

    public static Integer getSentenceRank(String sentence) throws IOException {
        URL obj = new URL("http://localhost:8090/sentences/check");
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = sentence.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        Integer result;
        Gson g = new Gson();
        SentenceInfo SentenceInfo = g.fromJson(response.toString(), SentenceInfo.class);
        result = SentenceInfo.getRank();

        return result;
    }

    public static String removeUnsupportedSymbols(String source) {
        return source.replaceAll("‘", "'")
                .replaceAll("’", "'")
                .replaceAll("\"", "'")
                .replaceAll("\\(=.*\\)", "");
    }
}
