package de.mazdermind.gintercom.clientsupport.hostid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class FileBasedHostId {
	private static final Logger log = LoggerFactory.getLogger(FileBasedHostId.class);
	private String hostId;

	@PostConstruct
	public void readOrCreateHostId() throws IOException {
		Path hostIdFilePath = Paths.get(System.getProperty("user.home"), ".gintercom-host-id");
		log.info("Using Host-ID File {}", hostIdFilePath);

		try {
			readHostId(hostIdFilePath);
		} catch (IOException e) {
			createNewHostId(hostIdFilePath);
		}
	}

	private void createNewHostId(Path hostIdFilePath) throws IOException {
		log.info("Unable to Read Host-ID from File, trying to create a new ID");
		String newHostId = RandomHostIdGenerator.generateRandomHostId();
		log.info("Generated new ID {}", newHostId);
		BufferedWriter outStream = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(hostIdFilePath)));
		outStream.write(newHostId);
		outStream.close();
		log.info("New ID saved to", hostIdFilePath);
	}

	private void readHostId(Path hostIdFilePath) throws IOException {
		InputStream inStream = Files.newInputStream(hostIdFilePath);
		hostId = new BufferedReader(new InputStreamReader(inStream)).readLine().trim();
		log.info("Read Host-ID {}", hostId);
	}

	public String getHostId() {
		return hostId;
	}
}
