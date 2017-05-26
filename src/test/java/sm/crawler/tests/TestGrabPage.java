package sm.crawler.tests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import sm.crawler.jsoup.GrabPage;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by smdeveloper on 5/25/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Jsoup.class)
public class TestGrabPage {

    @Test
    public void testGrab() throws Exception {
        //Create a real jsoup document using a test file
        String simpleHTML = new String(Files.readAllBytes(Paths.get(getClass().getResource("/simple.html").toURI())));
        Document doc = Jsoup.parse(simpleHTML);
        //Create a fake url to pass it to the jsoup parse static method
        URL fakeUrl = new URL("http://example.com/source1/page1");
        //Invoke Jsoup parse method
        mockStatic(Jsoup.class);
        PowerMockito.when(Jsoup.parse(eq(fakeUrl), anyInt())).thenReturn(doc);
        //Run the code to be tested
        GrabPage grabPage = new GrabPage(fakeUrl,1);
        grabPage.call();

        //Now check the results
        Set<URL> urls = grabPage.getUrlList();
        assertEquals(3, urls.size());
        assertTrue(urls.contains(new URL("http://example.com/source1/link1")));
        assertTrue(urls.contains(new URL("http://example.com/link2")));
        assertTrue(urls.contains(new URL("http://example.com/relative")));

    }

}
