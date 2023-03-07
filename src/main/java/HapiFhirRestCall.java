import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * <p>Hapi Fhir Call</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class HapiFhirRestCall {
    public static void main(String[] args) {
        List<File> files = Arrays.asList(Objects.requireNonNull(new File("output/fhir").listFiles()));
        Predicate<File> predicate = f -> f.getName().startsWith("practitionerInformation") || f.getName().startsWith("hospitalInformation");
        files.stream()
                .filter(predicate)
                .map(HapiFhirRestCall::readFileAsString)
                .forEach(HapiFhirRestCall::sendToFhirServer);

        files.stream()
                .filter(predicate.negate())
                .map(HapiFhirRestCall::readFileAsString)
                .forEach(HapiFhirRestCall::sendToFhirServer);
    }

    public static String readFileAsString(File f) {
        try {
            return new String(Files.readAllBytes(f.toPath()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void sendToFhirServer(String data) {
        try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
            //Execute and get the response.
            HttpPost httppost = new HttpPost("https://aist-partner.projekte.fh-hagenberg.at/pica-r4/fhir");
            httppost.setEntity(new StringEntity(data, ContentType.create("application/fhir+json")));
            HttpResponse response = httpclient.execute(httppost);
            System.out.println(response.getStatusLine());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
