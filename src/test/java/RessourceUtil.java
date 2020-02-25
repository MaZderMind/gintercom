import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RessourceUtil {
	private static final Logger log = LoggerFactory.getLogger(RessourceUtil.class);

	static String read(String filename) {
		try {
			URL resource = RessourceUtil.class.getResource(filename);
			URI uri = resource.toURI();
			Path path = Paths.get(uri);
			byte[] bytes = Files.readAllBytes(path);
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	static String readAndSubstituteIndex(String filename, int index) {
		String s = read(filename).replace("$", String.valueOf(index));
		log.info("readAndSubstituteIndex({}, {}) = \n{}", filename, index, s);
		return s;
	}
}
