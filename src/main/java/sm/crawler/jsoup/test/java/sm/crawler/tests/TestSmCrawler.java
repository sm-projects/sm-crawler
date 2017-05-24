package sm.crawler.tests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import sm.crawler.jsoup.CrawlerServer;
import sm.crawler.jsoup.GrabPage;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by smdeveloper on 5/23/17.
 */

public class TestSmCrawler {

    static final int SIZE = 5;
    @MockitoAnnotations.Mock
    ExecutorService mockExecutor;

    @InjectMocks
    CrawlerServer cserver = new CrawlerServer(2, SIZE);

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShouldVisit() throws IOException, InterruptedException, ExecutionException {
        Set<URL> urls = new HashSet<>();
        urls.add(new URL("http://ignore.com"));
        urls.add(new URL("http://example.com/normal"));
        urls.add(new URL("http://example.com/without#anchor"));
        urls.add(new URL("http://example.com/nopdfs.pdf"));
        urls.add(new URL("http://example.com/extra1"));
        urls.add(new URL("http://example.com/extra2"));
        urls.add(new URL("http://example.com/extra3"));

        //Mock out ah GrabPage so that we can control urls returned.
        GrabPage mockGrab = mock(GrabPage.class);
        when(mockGrab.getUrlList()).thenReturn(urls);
        //Mock a future object to  return our grabber.
        Future<GrabPage> mockFuture = mock(Future.class);
        when(mockFuture.isDone()).thenReturn(true);
        when(mockFuture.get()).thenReturn(mockGrab);
        //return our future
        when(mockExecutor.submit(any(Callable.class))).thenReturn(mockFuture);
        //Run the crawler server for example.com
        cserver.go(new URL("http://example.com"));
        Set<URL> visited = cserver.getMasterList();
        assertEquals(SIZE,visited.size());
        assertTrue(visited.contains(new URL("http://example.com/normal")));
        assertTrue(visited.contains(new URL("http://example.com/without")));
        assertTrue(visited.contains(new URL("http://example.com/without#anchor")));
        assertTrue(visited.contains(new URL("http://example.com/nopdfs.pdf")));
    }
}
