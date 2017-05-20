package sm.crawler;

import org.junit.Test;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import sm.crawler.jsoup.GrabPage;

public class TestGrabPage {

    @Test
    public void  testHackerNews() {
        ExecutorService exSvc = Executors.newSingleThreadExecutor();
        try {
         Future<GrabPage>  future = exSvc.submit(new GrabPage(new URL("https://news.ycombinator.com/")));
         GrabPage done = future.get();
         System.out.println("Running done.");
         done.dump();

        } catch(Exception e) {
            //Do nothing
        } finally {
            exSvc.shutdown();
        }
    }
}
