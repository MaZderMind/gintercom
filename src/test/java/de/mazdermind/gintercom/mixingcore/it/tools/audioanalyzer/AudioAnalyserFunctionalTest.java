package de.mazdermind.gintercom.mixingcore.it.tools.audioanalyzer;

import java.time.Duration;

import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.elements.AppSink;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import de.mazdermind.gintercom.mixingcore.it.tools.peakdetector.AppSinkSupport;

public class AudioAnalyserFunctionalTest {
	public static Duration SHORT_TIMEOUT = Duration.ofMillis(500);

	private AudioAnalyser audioAnalyser;
	private Pipeline pipeline;
	private Element src;

	@Before
	public void init() {
		Gst.init();

		audioAnalyser = new AudioAnalyser(48000, null);
		pipeline = (Pipeline) Gst.parseLaunch("" +
			"audiotestsrc is-live=true wave=sine name=src volume=0.2 ! " +
			"audio/x-raw,format=S16LE,channels=1,rate=48000 ! " +
			"appsink name=sink "
		);

		src = pipeline.getElementByName("src");

		AppSink sink = (AppSink) pipeline.getElementByName("sink");
		sink.set("emit-signals", true);
		sink.connect((AppSink.NEW_SAMPLE) appSink -> {
			long[] samples = AppSinkSupport.extractSampleValues(appSink.pullSample());
			audioAnalyser.appendSamples(samples);

			return FlowReturn.OK;
		});
	}

	@After
	public void teardownTestPipeline() {
		pipeline.stop();
	}

	@Test
	public void awaitFrequenciesAcceptsCorrectFrequency() {
		src.set("freq", 2000);
		pipeline.play();

		audioAnalyser.awaitFrequencies(ImmutableSet.of(2000.));
	}

	@Test(expected = AudioAnalyserException.class)
	public void awaitFrequenciesRejectsWrongFrequency() {
		src.set("freq", 2000);
		pipeline.play();

		audioAnalyser.awaitFrequencies(AudioAnalyser.DEFAULT_PERIOD, SHORT_TIMEOUT, ImmutableSet.of(400.));
	}

	@Test(expected = AudioAnalyserException.class)
	public void awaitFrequenciesDetectsNotEnoughData() {
		src.set("freq", 2000);
		src.set("num-buffers", 5);
		pipeline.play();

		audioAnalyser.awaitFrequencies(ImmutableSet.of(2000.));
	}

	@Test
	public void awaitSilenceAcceptsSilence() {
		src.set("volume", 0.0);
		pipeline.play();

		audioAnalyser.awaitSilence();
	}

	@Test(expected = AudioAnalyserException.class)
	public void awaitFrequenciesRejectsTone() {
		pipeline.play();

		audioAnalyser.awaitSilence(AudioAnalyser.DEFAULT_PERIOD, SHORT_TIMEOUT);
	}

	@Test(expected = AudioAnalyserException.class)
	public void awaitSilenceDetectsNotEnoughData() {
		src.set("num-buffers", 5);
		pipeline.play();

		audioAnalyser.awaitSilence();
	}

	@Test
	public void awaitDataAcceptsData() {
		pipeline.play();

		audioAnalyser.awaitData();
	}

	@Test(expected = AudioAnalyserException.class)
	public void awaitDataRejectsNoData() {
		src.set("num-buffers", 0);
		pipeline.play();

		audioAnalyser.awaitData(SHORT_TIMEOUT);
	}

	@Test
	public void awaitNoDataAcceptsNoDataAtAll() {
		src.set("num-buffers", 0);
		pipeline.play();

		audioAnalyser.awaitNoData();
	}

	@Test
	public void awaitNoDataAcceptsStopOfData() {
		src.set("num-buffers", 10);
		pipeline.play();

		audioAnalyser.awaitNoData();
	}

	@Test(expected = AudioAnalyserException.class)
	public void awaitNoDataRejectsData() {
		pipeline.play();

		audioAnalyser.awaitNoData(AudioAnalyser.DEFAULT_PERIOD, SHORT_TIMEOUT);
	}

}
