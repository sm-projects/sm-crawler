package sm.crawler.jsoup;

import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by smdeveloper on 5/20/17.
 */
public class CrawlerServer  {
    public static final int THREAD_COUNT = 5;
    public static final long PAUSE_TIME = 1000;

    private Set<URL> masterList = new HashSet<>();
    private List<Future<GrabPage>> futures = new ArrayList<>();
    private ExecutorService exeSvc = Executors.newFixedThreadPool(THREAD_COUNT);

    private String urlBase;
    private final int maxDepth;
    private final int maxUrls;

    //Constructor
    public CrawlerServer(int amaxDepth, int amaxUrls) {
        this.maxDepth = amaxDepth;
        this.maxUrls = amaxUrls;
    }

    /**
     *
     * @param start
     */
    public void go(URL start) throws IOException, InterruptedException {
       //Stay within the same website
        urlBase = start.toString().replaceAll("(.*//.*/).*", "$1");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        submitNewURL(start,0);

        while(checkPageGrabs());
        stopWatch.stop();

        System.out.println("Found " + masterList.size() + " urls");
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");
    }

    /**
     *
     * @return
     * @throws InterruptedException
     */
    private boolean checkPageGrabs() throws InterruptedException {
        Thread.sleep(PAUSE_TIME);
        Set<sm.crawler.jsoup.GrabPage> pageSet = new HashSet<>();
        Iterator<Future<sm.crawler.jsoup.GrabPage>> iter = futures.iterator();

        while(iter.hasNext()) {
            Future<GrabPage> item = iter.next();
            if(item.isDone()) {
                iter.remove();
                try {
                    pageSet.add(item.get());
                } catch (InterruptedException ioe) {
                } catch(ExecutionException ee) {}
            }
        }
        for(GrabPage pg : pageSet ) {
            addNewURLs(pg);
        }

        return (futures.size() > 0);
    }

    /**
     *
     * @param gpg
     */
    private void addNewURLs(GrabPage gpg) {
       for(;;) {

       }
    }

    /**
     *
     * @param aurl
     * @param depth
     */
    private void submitNewURL(URL aurl, int depth) {

    }
}

