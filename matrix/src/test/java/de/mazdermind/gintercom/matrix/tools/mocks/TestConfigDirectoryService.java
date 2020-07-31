package de.mazdermind.gintercom.matrix.tools.mocks;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import de.mazdermind.gintercom.matrix.configuration.ConfigDirectoryService;
import lombok.Getter;
import lombok.SneakyThrows;

@TestComponent
@Primary
public class TestConfigDirectoryService extends ConfigDirectoryService {
	@Getter
	private Path configDirectory;

	@Getter
	private FileSystem fileSystem;

	public TestConfigDirectoryService() {
		super(null);
		reset();
	}

	@SneakyThrows
	public void reset() {
		fileSystem = Jimfs.newFileSystem(Configuration.unix());
		configDirectory = fileSystem.getPath("/etc/gintercom");
		Files.createDirectories(configDirectory);
	}
}
