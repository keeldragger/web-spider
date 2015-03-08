package com.cedarsoftware.webspider

import com.cedarsoftware.util.UrlUtilities
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class WebSpider
{
    static
    final String USER_AGENT = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
    private static final Logger LOG = LogManager.getLogger(WebSpider.class)

    WebSpider()
    {
        UrlUtilities.userAgent = USER_AGENT;
    }

    void crawl(Receiver receiver)
    {
        final Deque<String> stack = new ArrayDeque<>()
        stack.addFirst("http://www.myotherdrive.com")
        Set<String> visited = new HashSet<>()

        while (!stack.isEmpty())
        {
            final String url = stack.removeFirst()

            if (visited.contains(url))
            {
                continue
            }

            visited.add(url)

            try
            {
                Document doc = Jsoup.connect(url).get()
                receiver.receive(url, doc.toString(), System.currentTimeMillis())
                Map contents = extractUrls(doc)
                for (Link link in contents.links)
                {
                    stack.add(link.url)
                }
            }
            catch (Exception e)
            {
                println 'error, url: ' + url
                e.printStackTrace()
            }
        }
    }

    /**
     * Return links from passed in Jsoup Document
     * @param doc Document that was fetched by Jsoup
     * @return Map containing all links:
     * [a: [],
     *  script: [],
     *  img: [],
     *  input: []
     *  ...
     *  ]
     */
    Map extractUrls(Document doc)
    {
        Map contents = [:]
        Set<Link> links = []

        Elements anchors = doc.select("a[href]")
        Elements media = doc.select("[src]")
        Elements imports = doc.select("link[href]")

        print("\nLinks: (%d)", links.size())
        for (Element anchor : anchors)
        {
            Link link = new Link([url:anchor.attr('abs:href'), text:anchor.text()])
            links.add(link)
        }

        contents.links = links

//        print("\nMedia: (%d)", media.size())
//        for (Element src : media)
//        {
//            if (src.tagName().equals("img"))
//            {
//                print(" * %s: <%s> %sx%s (%s)",
//                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
//                        trim(src.attr("alt"), 20))
//            }
//            else
//            {
//                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"))
//            };
//        }
//
//        print("\nImports: (%d)", imports.size())
//        for (Element link : imports)
//        {
//            print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"))
//        }


        return contents
    }

    private static void print(String msg, Object... args)
    {
        System.out.println(String.format(msg, args))
    }

    private static String trim(String s, int width)
    {
        if (s.length() > width)
        {
            return s.substring(0, width - 1) + "."
        }
        else
        {
            return s
        };
    }
}