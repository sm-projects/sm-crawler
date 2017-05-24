package sm.crawler.jsoup;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * GrabPage is the task called by the CrawlerServer.
 *
 */

public class GrabPage implements Callable<GrabPage> {
    static final int TIMEOUT = 60000; //1 min

    private  URL url;
    private int depth;
    private  Set<URL> urlSet = new HashSet<>();

    //CONSTRUCTOR
    public GrabPage(URL aurl, int adepth) {
        this.url = aurl;
        this.depth = adepth;
    }

    @Override
    public GrabPage call() throws Exception {
       Document doc = Jsoup.parse(this.url, TIMEOUT);
       processLinks(doc.select("a[href]"));
       return this;
    }

    /**
     *
     * @param alinks
     */
    private void processLinks(Elements alinks) {
        System.out.println("****************** : " + alinks.size());
        for (Element link : alinks) {
            String href = link.attr("href");
            if (StringUtils.isBlank(href) || href.startsWith("#")) {
                continue;
            }
            try {
                URL nextUrl = new URL(url, href);
                urlSet.add(nextUrl);
                System.out.println("Added url--->: " + nextUrl);
            } catch (MalformedURLException e) {
                //Just ignore bad urls for nowhttps://github.com/sm-projects/sm-crawler
            }
        }
    }

    /**
     *
     * @return Set<URL> the set of urls to be visited.
     */
    public Set<URL> getUrlList() {
        return this.urlSet;
    }

    /**
     *
     * @return int the depth specified
     */
    public int getDepth() {
        return this.depth;
    }

    public void dump() {
           for (URL url1 : urlSet) {
               System.out.println("Links to : " + url1.toString());
           }
    }

}//end
