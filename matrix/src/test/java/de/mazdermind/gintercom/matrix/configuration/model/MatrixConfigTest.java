package de.mazdermind.gintercom.matrix.configuration.model;

import static de.mazdermind.gintercom.testutils.JsonMapUtils.convertJsonTo;
import static de.mazdermind.gintercom.testutils.assertations.IsValidCondition.VALID;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import de.mazdermind.gintercom.testutils.JsonMap;
import de.mazdermind.gintercom.testutils.JsonMapUtils;

public class MatrixConfigTest {

	private JsonMap testJson;

	@Before
	public void prepare() {
		testJson = JsonMapUtils.readTomlToMap("config/model/matrix.toml");
	}


	@Test
	public void deserializesCorrectly() {
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig.getDisplay()).isEqualTo("C3VOC Intercom");

		assertThat(matrixConfig.getWebui().getBind().getHostAddress()).isEqualTo("0.0.0.0");
		assertThat(matrixConfig.getWebui().getPort()).isEqualTo(2380);

		assertThat(matrixConfig.getRtp().getJitterbuffer()).isEqualTo(50L);

		assertThat(matrixConfig.getPorts().getPanelToMatrix().getStart()).isEqualTo(20000);
		assertThat(matrixConfig.getPorts().getPanelToMatrix().getLimit()).isEqualTo(9999);
		assertThat(matrixConfig.getPorts().getMatrixToPanel().getStart()).isEqualTo(40000);
		assertThat(matrixConfig.getPorts().getMatrixToPanel().getLimit()).isEqualTo(9999);
	}


	@Test
	public void deserializesIpv6Correctly() {
		testJson.getObject("webui").put("bind", "2a03:4000:6:d080::1");

		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig.getWebui().getBind().getHostAddress()).isEqualTo("2a03:4000:6:d080:0:0:0:1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void deserializationFailsWithInvalidAddress() {
		testJson.getObject("webui").put("bind", "127.0..");
		convertJsonTo(MatrixConfig.class, testJson);
	}

	@Test
	public void validationSucceeds() {
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).is(VALID);
	}

	@Test
	public void validationFailsWithoutBindAddress() {
		testJson.getObject("webui").remove("bind");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutPort() {
		testJson.getObject("webui").remove("port");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutDisplay() {
		testJson.remove("display");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutJitterbufffer() {
		testJson.getObject("rtp").remove("jitterbuffer");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutWebuiSection() {
		testJson.remove("webui");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutRtpSection() {
		testJson.remove("rtp");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutPanelToMatrixConfig() {
		testJson.getObject("ports").remove("panelToMatrix");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutMatrixToPanelConfig() {
		testJson.getObject("ports").remove("matrixToPanel");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithInvalidPanelToMatrixConfig() {
		testJson.getObject("ports").getObject("panelToMatrix").remove("start");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithInvalidMatrixToPanelConfig() {
		testJson.getObject("ports").getObject("matrixToPanel").remove("start");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}

	@Test
	public void validationFailsWithoutPortsConfig() {
		testJson.remove("ports");
		MatrixConfig matrixConfig = convertJsonTo(MatrixConfig.class, testJson);
		assertThat(matrixConfig).isNot(VALID);
	}
}
