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



public class GrabPage implements Callable<GrabPage> {
    static final int TIMEOUT = 60000; //1 min

    private  URL url;
    private  Set<URL> urlSet = new HashSet<>();

    public GrabPage(URL aurl) {
        this.url = aurl;
    }

    @Override
    public GrabPage call() throws Exception {
       Document doc = Jsoup.parse(this.url, TIMEOUT);
       Elements links = doc.select("a[href]");

       for (Element link : links) {
           String href = link.attr("href");
           if (StringUtils.isBlank(href)) {
               continue;
           }
           try {
                URL nextUrl = new URL(url, href);
                urlSet.add(nextUrl);
           } catch (MalformedURLException e) {
               //Just ignore bad urls for now
           }
       }
       return this;
    }
    public void dump() {
           for (URL url1 : urlSet) {
               System.out.println("Links to : " + url1.toString());
           }
    }

}//end
