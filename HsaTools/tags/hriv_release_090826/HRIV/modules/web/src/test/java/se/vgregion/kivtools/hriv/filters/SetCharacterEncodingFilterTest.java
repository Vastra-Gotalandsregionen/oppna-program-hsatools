package se.vgregion.kivtools.hriv.filters;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class SetCharacterEncodingFilterTest {
  private SetCharacterEncodingFilter filter;

  @Before
  public void setUp() throws Exception {
    filter = new SetCharacterEncodingFilter();
  }

  @Test
  public void testInit() {
    try {
      filter.init(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    MockFilterConfig filterConfig = new MockFilterConfig();
    filter.init(filterConfig);

    filterConfig.addInitParameter("ignore", "true");
    filter.init(filterConfig);

    filterConfig.addInitParameter("ignore", "yes");
    filter.init(filterConfig);

    filterConfig.addInitParameter("ignore", "unknown");
    filter.init(filterConfig);
  }

  @Test
  public void testSelectEncoding() {
    assertNull(filter.selectEncoding(null));

    MockFilterConfig filterConfig = new MockFilterConfig();
    filterConfig.addInitParameter("encoding", "UTF-8");
    filter.init(filterConfig);

    assertEquals("UTF-8", filter.selectEncoding(null));

    filterConfig.addInitParameter("encoding", "ISO-8859-1");
    filter.init(filterConfig);

    assertEquals("ISO-8859-1", filter.selectEncoding(null));

    filter.destroy();

    assertNull(filter.selectEncoding(null));
  }

  @Test
  public void testDoFilter() throws IOException, ServletException {
    try {
      filter.doFilter(null, null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    MockHttpServletRequest servletRequest = new MockHttpServletRequest();
    MockFilterConfig filterConfig = new MockFilterConfig();
    filterConfig.addInitParameter("ignore", "false");
    filter.init(filterConfig);
    try {
      filter.doFilter(servletRequest, null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    servletRequest.setCharacterEncoding("ISO-8859-1");
    try {
      filter.doFilter(servletRequest, null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    filterConfig = new MockFilterConfig();
    filterConfig.addInitParameter("ignore", "true");
    filter.init(filterConfig);
    try {
      filter.doFilter(servletRequest, null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    MockHttpServletResponse servletResponse = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();
    filter.doFilter(servletRequest, servletResponse, filterChain);
    assertEquals("ISO-8859-1", servletRequest.getCharacterEncoding());

    filterConfig.addInitParameter("encoding", "UTF-8");
    filterConfig.addInitParameter("ignore", "true");
    filter.init(filterConfig);
    filterChain = new MockFilterChain();
    filter.doFilter(servletRequest, servletResponse, filterChain);
    assertEquals("UTF-8", servletRequest.getCharacterEncoding());
  }
}
