package sm.crawler.jsoup;

import java.io.IOException;
import java.net.URL;

public  class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        CrawlerServer cServer = new CrawlerServer(2,100);
        System.out.println("Running crawler...");
        cServer.go(new URL("http://news.ycombinator.com"));
        cServer.write("/home/smdeveloper/urllist.txt");
    }

}
