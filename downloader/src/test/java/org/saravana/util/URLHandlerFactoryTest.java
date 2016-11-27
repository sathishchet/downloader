package org.saravana.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URLStreamHandler;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class URLHandlerFactoryTest {

	private URLHandlerFactory factory;

	@Before
	@SuppressWarnings("serial")
	public void setUp() throws Exception {
		factory = spy(new URLHandlerFactory());
		factory.setUrlHandlers(new HashSet<String>() {
			{
				add("telnet:org.saravana.TelnetHandler");
				add("tcp:org.saravana.TcpHandler");
			}
		});
	}

	@Test
	public void initialise_Test() {
		try {
			assertEquals(null, factory.getHandler("http"));
			verify(factory).getHandler("http");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			fail("Exception: " + e.getMessage());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void unknown_handler_Test() {
		try {
			assertEquals(null, factory.getHandler("qwery"));
			verify(factory).getHandler("http");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			fail("Exception: " + e.getMessage());
		}
	}

	@Test(expected = AssertionError.class)
	public void initialise_Error_Handler_Test() {
		try {
			assertEquals(null, factory.getHandler("telnet"));
			verify(factory).getHandler("telnet");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			fail("Exception: " + e.getMessage());
		}
	}

	@Test
	public void initialise_Handler_Test() {
		try {
			URLStreamHandler handler = factory.getHandler("telnet");
			assertNotNull(handler);
			assertEquals("org.saravana.TelnetHandler", handler.getClass().getName());
			verify(factory).getHandler("telnet");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			fail("Exception: " + e.getMessage());
		}
	}

	@Test
	public void initialise_Handler_Cache_Test() {
		try {
			URLStreamHandler handler = factory.getHandler("telnet");
			URLStreamHandler handler2 = factory.getHandler("telnet");

			assertNotNull(handler);
			assertEquals("org.saravana.TelnetHandler", handler.getClass().getName());
			verify(factory, times(2)).getHandler("telnet");
			assertSame(handler, handler2);// same instance from cache
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			fail("Exception: " + e.getMessage());
		}
	}
}
