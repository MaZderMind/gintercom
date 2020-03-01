import org.freedesktop.gstreamer.Gst;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mazdermind.gintercom.matrix.Group;
import de.mazdermind.gintercom.matrix.Matrix;
import de.mazdermind.gintercom.matrix.MatrixFactory;
import de.mazdermind.gintercom.matrix.Panel;

public class MixingIT {
	private static final Logger log = LoggerFactory.getLogger(MixingIT.class);
	private Matrix matrix;

	@Before
	public void before() {
		Gst.init();
		matrix = MatrixFactory.getInstance();

	}

	@Test
	public void testMixing() throws InterruptedException {
		Group group0 = matrix.addGroup("0");
		Group group1 = matrix.addGroup("1");

		Panel panel0 = matrix.addPanel("0", "127.0.0.1", 40000, 50000);
		Panel panel1 = matrix.addPanel("1", "127.0.0.1", 40001, 50001);
		Panel panel2 = matrix.addPanel("2", "127.0.0.1", 40002, 50002);
		Panel panel3 = matrix.addPanel("3", "127.0.0.1", 40003, 50003);

		panel0.startTransmittingTo(group0);
		panel1.startTransmittingTo(group1);
		panel2.startReceivingFrom(group0);
		panel3.startReceivingFrom(group1);

		panel0.remove();
		panel1.remove();
		panel2.remove();
		panel3.remove();

		group0.remove();
		group1.remove();
	}
}
