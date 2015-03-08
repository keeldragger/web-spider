package com.cedarsoftware.webspider

import com.cedarsoftware.util.EncryptionUtilities
import com.cedarsoftware.util.StringUtilities
import com.cedarsoftware.util.UrlUtilities
import groovy.transform.CompileStatic
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.UnsupportedMimeTypeException
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import javax.net.ssl.SSLHandshakeException
import java.util.regex.Pattern

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
@CompileStatic
class WebSpider
{
    static final String USER_AGENT = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
    private static final Logger LOG = LogManager.getLogger(WebSpider.class)
    private static final Pattern emailPattern = ~/.+\@.+\..+/

    WebSpider()
    {
        UrlUtilities.userAgent = USER_AGENT
    }

    void crawl(Receiver receiver)
    {
        final Deque<Anchor> stack = new ArrayDeque<>()
        stack.addFirst(new Anchor(url:'http://groovy-lang.org/', text:'root'))
        Set<String> visited = new HashSet<>()

        while (!stack.isEmpty())
        {
            final Anchor anchor = stack.removeFirst()

            if (beenThere(anchor.url, visited))
            {   // Don't redo site already processed.
                continue
            }

            visited.add(anchor.url.length() <= 40 ? anchor.url : EncryptionUtilities.calculateSHA1Hash(anchor.url.bytes))
            try
            {
                // Snag HTML Document at URL
                Document doc = Jsoup.connect(anchor.url).userAgent(USER_AGENT).followRedirects(true).get()
                receiver.html(anchor.url, anchor.text, doc, System.currentTimeMillis())
                Map<String, Set<Anchor>> contents = extractUrls(doc)
                for (Anchor link in contents.links)
                {
                    if (StringUtilities.hasContent(link.url) && !beenThere(link.url, visited))
                    {   // Skip empty or blank urls.
                        stack.add(link)
                    }
                }
            }
            catch (HttpStatusException e)
            {
                LOG.info('HTTP status exception (' + e.statusCode + '), url: ' + anchor.url + ', ' + e.message)
            }
            catch (MalformedURLException e)
            {
                if (anchor.url.toLowerCase().startsWith("mailto:"))
                {
                    String email = anchor.url.substring(7)
                    if (emailPattern.matcher(email).find())
                    {
                        receiver.mailto(anchor.text, email)
                    }
                }
                else
                {
                    LOG.info('Malformed URL, url: ' + anchor.url + ', msg: ' + e.message)
                }
            }
            catch (UnsupportedMimeTypeException e)
            {
                String mimeType = e.mimeType.toLowerCase()
                if (mimeType.startsWith("application/pdf"))
                {
                    receiver.pdf(anchor.url, anchor.text)
                }
                else if (mimeType.startsWith("image/"))
                {
                    receiver.image(anchor.url, anchor.text, mimeType.substring(6))
                }
                else if (mimeType.startsWith("application/zip"))
                {
                    receiver.zip(anchor.url, anchor.text)
                }
                else if (mimeType.startsWith("application/java-archive"))
                {
                    receiver.jar(anchor.url, anchor.text)
                }
                else
                {
                    LOG.info('Unsupported mime type (' + e.mimeType + '), url: ' + anchor.url)
                }
            }
            catch (SocketTimeoutException e)
            {
                LOG.info('Socket timeout, url: ' + anchor.url + ', msg: ' + e.message)
            }
            catch (SSLHandshakeException e)
            {
                LOG.info('SSL Handshake error, url: ' + anchor.url + ', msg: ' + e.message)
            }
            catch (IOException e)
            {
                LOG.info('IO Exception, url: ' + anchor.url + ', msg: ' + e.message)
            }
            catch (IllegalArgumentException e)
            {
                LOG.info('Illegal argument exception, url: ' + anchor.url + ', msg: ' + e.message)
            }
            catch (Exception e)
            {
                println 'error, url: ' + anchor
                e.printStackTrace()
            }
        }
    }

    boolean beenThere(String url, Set<String> visited)
    {
        return visited.contains(url) || visited.contains(EncryptionUtilities.calculateSHA1Hash(url.bytes))
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
        Set<Anchor> links = []

        Elements anchors = doc.select("a[href]")
        Elements media = doc.select("[src]")
        Elements imports = doc.select("link[href]")

//        print("\nLinks: (%d)", links.size())
        for (Element anchor : anchors)
        {
            Anchor link = new Anchor([url:anchor.attr('abs:href'), text:anchor.text()])
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
//            }
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