package pl.baczkowicz.mqtt.spy.versions;

import static org.junit.Assert.*;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.junit.Before;
import org.junit.Test;

public class VersionComparison
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public final void test()
	{
		final String v8b1 = "0.0.8-beta-1";
		final String v8b10 = "0.0.8-beta-10";
		final String v8b2 = "0.0.8-beta-2";
		final String v8 = "0.0.8-11";
		
		assertTrue(0 == new DefaultArtifactVersion(v8b1).compareTo(new DefaultArtifactVersion(v8b1)));
		assertTrue(0 > new DefaultArtifactVersion(v8b1).compareTo(new DefaultArtifactVersion(v8b10)));
		assertTrue(0 > new DefaultArtifactVersion(v8b1).compareTo(new DefaultArtifactVersion(v8b2)));
		assertTrue(0 > new DefaultArtifactVersion(v8b2).compareTo(new DefaultArtifactVersion(v8b10)));
		assertTrue(0 > new DefaultArtifactVersion(v8b2).compareTo(new DefaultArtifactVersion(v8)));
	}
}
