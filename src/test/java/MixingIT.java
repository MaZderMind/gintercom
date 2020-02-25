import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.freedesktop.gstreamer.Bin;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.StateChangeReturn;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MixingIT {
	private static final Logger log = LoggerFactory.getLogger(MixingIT.class);

	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
	private Pipeline pipeline;
	private Caps caps;

	@Before
	public void before() throws IOException {
		setupDotDir();
		Gst.init();
		caps = Caps.fromString("audio/x-raw,format=S16LE,rate=48000,channels=1");

		pipeline = new Pipeline("gintercom-matrix");
		pipeline.getBus().connect((Bus.ERROR) (source, code, message) -> log.error(message));

		log.info("pipeline.play()");
		assertThat(pipeline.play()).isEqualTo(StateChangeReturn.SUCCESS);
		debug("initial");
	}

	private void setupDotDir() throws IOException {
		String dateString = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		Path dir = Paths.get("/tmp/intercom-group-problem-test/", dateString);
		Files.createDirectories(dir);
		log.info("creating dot-files in {}", dir);
		environmentVariables.set("GST_DEBUG_DUMP_DOT_DIR", String.valueOf(dir.toAbsolutePath()));
	}

	@After
	public void after() {
		assertThat(pipeline.stop()).isEqualTo(StateChangeReturn.SUCCESS);
	}


	@Test
	public void testMixing() throws InterruptedException {
		// FIXME audiomixer start-time

		addGroup(0);
		addGroup(1);

		addPanel(0);
		addPanel(1);
		addPanel(2);
		addPanel(3);

		link("rx0", "grp0_in");
		link("rx1", "grp1_in");
		link("grp0_out", "tx2");
		link("grp1_out", "tx3");

		Thread.sleep(60_000);
	}

	private void link(String from, String to) {
		log.info("link(from={}, to={})", from, to);

		Element fromEl = pipeline.getElementByName(from);
		assertThat(fromEl).isNotNull();

		Element toEl = pipeline.getElementByName(to);
		assertThat(toEl).isNotNull();

		boolean result = fromEl.linkFiltered(toEl, caps);
		assertThat(result).isTrue();
		debug("link-" + from + "-to-" + to);
	}

	private void addGroup(int index) {
		log.info("addGroup({})", index);
		addTemplate("group.txt", index);

		debug(String.format("after-add-group%d", index));
	}

	private void debug(String filename) {
		pipeline.debugToDotFile(Bin.DebugGraphDetails.SHOW_ALL, filename);
	}

	private void addPanel(int index) {
		log.info("addPanel({})", index);
		addTemplate("panel_tx.txt", index);
		addTemplate("panel_rx.txt", index);

		debug(String.format("after-add-panel%d", index));
	}

	private void addTemplate(String filename, int index) {
		String binDescription = RessourceUtil.readAndSubstituteIndex(filename, index);
		Bin bin = Gst.parseBinFromDescription(binDescription, true);
		assertThat(bin).isNotNull();
		assertThat(pipeline.add(bin)).isTrue();
		bin.syncStateWithParent();
	}
}
