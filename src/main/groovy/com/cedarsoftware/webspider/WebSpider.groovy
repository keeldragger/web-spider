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
        stack.addFirst(new Anchor(url:'https://github.com/jdereg/json-io', text:'experian', container: (Document) null))
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
                Document doc = Jsoup.connect(anchor.url).userAgent(USER_AGENT).followRedirects(true).timeout(10000).get()
                Set<Anchor> links = extractUrls(doc)
                receiver.processLink(anchor, doc, System.currentTimeMillis())

                for (Anchor link in links)
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
                        receiver.processMailto(anchor, email, now())
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
                    receiver.processPDF(anchor, now())
                }
                else if (mimeType.startsWith("image/"))
                {
                    receiver.processImage(anchor, mimeType.substring(6), now())
                }
                else if (mimeType.startsWith("application/zip") || mimeType.startsWith("application/x-gzip"))
                {
                    receiver.processZip(anchor, now())
                }
                else if (mimeType.startsWith("application/java-archive"))
                {
                    receiver.processJar(anchor, now())
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
     * @return Map containing all links
     */
    Set<Anchor> extractUrls(Document doc)
    {
        Set<Anchor> links = []
        Elements anchors = doc.select("a[href]")

        for (Element anchor : anchors)
        {
            Anchor link = new Anchor([url:anchor.attr('abs:href'), text:anchor.text(), container: doc])
            links.add(link)
        }

        return links
    }

    long now()
    {
        System.currentTimeMillis()
    }
}