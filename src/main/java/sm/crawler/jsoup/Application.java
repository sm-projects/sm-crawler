package sm.crawler.jsoup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public  class Application {

    public static void main(String[] args) {

        ExecutorService exSvc = Executors.newSingleThreadExecutor();

        try {
            // Future<GrabPage> future = exSvc.submit(new GrabPage(new URL("https://news.ycombinator.com/")));
            Future<GrabPage> future = exSvc.submit(new GrabPage(new URL("http://bbc.com/")));

            GrabPage done = future.get();
            done.dump();
        } catch (Exception e) {
           //Do nothing
           e.printStackTrace();
        } finally {
            if (exSvc != null) {
                exSvc.shutdown();
            }
        }

    }

}
