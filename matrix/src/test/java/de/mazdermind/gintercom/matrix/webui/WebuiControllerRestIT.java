package de.mazdermind.gintercom.matrix.webui;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;

import org.apache.http.HttpStatus;
import org.junit.Test;

import de.mazdermind.gintercom.matrix.RestTestBase;
import io.restassured.http.ContentType;

public class WebuiControllerRestIT extends RestTestBase {
	private static void assertReturnsHtml(String originalUrl) {
		given()
			.redirects().follow(false)
			.get(originalUrl)
			.then()
			.statusCode(HttpStatus.SC_OK)
			.contentType(ContentType.HTML)
			.body(containsString("<app-root>"));
	}

	private static void assertRedirectsToUi(String originalUrl) {
		given()
			.redirects().follow(false)
			.get(originalUrl)
			.then()
			.statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
			.header("Location", endsWith("/ui/"));
	}

	@Test
	public void redirectsToUiOnDomainRoot() {
		assertRedirectsToUi("");
	}

	@Test
	public void redirectsToUiOnSlash() {
		assertRedirectsToUi("/");
	}

	@Test
	public void returnsHtmlOnUiWithoutSlash() {
		assertReturnsHtml("/ui");
	}

	@Test
	public void returnsHtmlOnSlashUiSlash() {
		assertReturnsHtml("/ui/");
	}

	@Test
	public void returnsHtmlOnSlashUiSlashFoo() {
		assertReturnsHtml("/ui/foo");
	}

	@Test
	public void returnsHtmlOnSlashUiSlashFooSlashMoo() {
		assertReturnsHtml("/ui/foo/moo");
	}
}
