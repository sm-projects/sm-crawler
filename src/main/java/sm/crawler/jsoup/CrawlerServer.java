package sm.crawler.jsoup;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
        //COMMENT - PRINT OUT
        System.out.println(urlBase);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        submitNewURL(start,0);

        while(checkPageGrabs());
        stopWatch.stop();
        //COMMENT - PRINT OUT
        System.out.println("Finished grabbing pages...");

        System.out.println("Found " + masterList.size() + " urls");
        System.out.println("in " + stopWatch.getTime() / 1000 + " seconds");
    }

    /**
     * Checks the status of the threadpools, by doing the following
     *
     * 1. Sleep for a little while.  Note: when polling tasks in other threads, you have to inject some sort of sleep,
     * otherwise your manager thread uses all the resources.
     * 2. Loop through the futures.
     * 3. When a future is done, remove it from the polling list.
     * 4. If there is an execution exception or a time out, then the task is just removed from the list.  We can flesh this out later
     *    for our crawler stats.  For now, we just need to keep the list clean.
     * 5. Go through the completed GrabPage objects looking for more URLs to process.
     * 6. Return true if there are still futures to process.
     *
     * @return
     * @throws InterruptedException
     */
    private boolean checkPageGrabs() throws InterruptedException {
        Thread.sleep(PAUSE_TIME);
        Set<GrabPage> pageSet = new HashSet<>();
        Iterator<Future<GrabPage>> iter = futures.iterator();

        while(iter.hasNext()) {
            Future<GrabPage> item = iter.next();
            if(item.isDone()) {
                iter.remove();
                try {
                    pageSet.add(item.get());
                } catch (InterruptedException ioe) {
                    ioe.printStackTrace();
                } catch(ExecutionException ee) {
                    ee.printStackTrace();
                }
            }
        }
        for(GrabPage pg : pageSet ) {
            addNewURLs(pg);
        }

        return (futures.size() > 0);
    }

    /**
     *
     * @param aPage
     */
    private void addNewURLs(GrabPage aPage) {
       for(URL currUrl : aPage.getUrlList()) {
           if(currUrl.toString().contains("#")) {
               try  {
                   currUrl = new URL(StringUtils.substringBefore(currUrl.toString(), "#"));
               } catch(MalformedURLException male) {}
           }
           submitNewURL(currUrl, aPage.getDepth() + 1);
       }
    }

    /**
     *  This method creates a GrabPage if needed and submits the task to the executor service (exvSvc)
     *
     * @param aurl
     * @param depth
     */
    private void submitNewURL(URL aurl, int depth) {
        if(shouldVisit(aurl,depth)) {
            masterList.add(aurl);
            GrabPage gPage = new GrabPage(aurl, depth);
            Future<GrabPage> futureGb = this.exeSvc.submit(gPage);
            futures.add(futureGb);
        }
    }

    /**
     * A filter for sites that needs to be visited or not.
     *
     * @param url
     * @param depth
     * @return
     */
    private boolean shouldVisit(URL url, int depth) {
        if (masterList.contains(url)) { //already visited.
           return false;
       }
       if(!url.toString().startsWith(urlBase)) {//Must be part of the same site
           return false;
       }
       if(url.toString().endsWith(".pdf")) { //filter out pdf files
            return false;
       }
       if(depth > maxDepth) { //exceeded max depth limit
           return false;
       }
       if(masterList.size() >= maxUrls) {//exceeded max url to ve visitied limit
           return false;
       }

       return true;
    }

    /**
     * Returns the master list of urls visited.
     * @return
     */
    public Set<URL> getMasterList() {
        return this.masterList;
    }
    /**
     *
     * @param path
     * @throws IOException
     */
    public void write(String path) throws IOException {
        FileUtils.writeLines(new File(path), masterList);
    }
}

