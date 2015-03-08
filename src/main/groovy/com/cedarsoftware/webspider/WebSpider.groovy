package com.cedarsoftware.webspider

import com.cedarsoftware.util.StringUtilities
import com.cedarsoftware.util.UrlUtilities
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import javax.net.ssl.SSLHandshakeException

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class WebSpider
{
    static final String USER_AGENT = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
    private static final Logger LOG = LogManager.getLogger(WebSpider.class)

    WebSpider()
    {
        UrlUtilities.userAgent = USER_AGENT;
    }

    void crawl(Receiver receiver)
    {
        final Deque<String> stack = new ArrayDeque<>()
        stack.addFirst("http://www.pricewatch.com/")
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
                Document doc = Jsoup.connect(url).userAgent(USER_AGENT).followRedirects(true).get()
                receiver.digest(url, doc.location(), doc.title(), doc.outerHtml(), System.currentTimeMillis())
                Map contents = extractUrls(doc)
                for (Link link in contents.links)
                {
                    if (StringUtilities.hasContent(link.url) && !visited.contains(link.url))
                    {   // Skip empty or blank urls.
                        stack.add(link.url)
                    }
                }
            }
            catch (HttpStatusException e)
            {
                LOG.info('HTTP status exception (' + e.statusCode + '), url: ' + url + ', ' + e.message)
            }
            catch (MalformedURLException e)
            {
                if (url.toLowerCase().startsWith("mailto:"))
                {
                    receiver.mailto(url.substring(7));
                }
                LOG.info('Malformed URL, url: ' + url + ', msg: ' + e.message)
            }
            catch (UnsupportedMimeTypeException e)
            {
                if (e.mimeType.startsWith("application/pdf"))
                {

                }
                LOG.info('Unsupported mime type (' + e.mimeType + '), url: ' + url)
            }
            catch (SocketTimeoutException e)
            {
                LOG.info('Socket timeout, url: ' + url + ', msg: ' + e.message)
            }
            catch (SSLHandshakeException e)
            {
                LOG.info('SSL Handshake error, url: ' + url + ', msg: ' + e.message)
            }
            catch (IOException e)
            {
                LOG.info('IO Exception, url: ' + url + ', msg: ' + e.message)
            }
            catch (IllegalArgumentException e)
            {
                LOG.info('Illegal argument exception, url: ' + url + ', msg: ' + e.message)
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

//        print("\nLinks: (%d)", links.size())
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
}