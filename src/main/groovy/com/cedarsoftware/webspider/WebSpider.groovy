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

import java.util.regex.Pattern

/**
 * WebSpider allows you to spider (or crawl) a set of urls, ad-infinitim.  The URLs are passed in as roots, with
 * some extra 'control' information, indicating such things as to bind the spider to the confines of the domain
 * or to let it go where the wind (links) take it.
 *
 * @author John DeRegnaucourt (john@cedarsoftware.com)
 *         <br>
 *         Copyright (c) Cedar Software LLC
 *         <br><br>
 *         Licensed under the Apache License, Version 2.0 (the "License")
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *         <br><br>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         <br><br>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 */
@CompileStatic
class WebSpider
{
    static final String USER_AGENT = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
    private static final int sha1Length = 40
    private static final Logger LOG = LogManager.getLogger(WebSpider.class)
    private static final Pattern emailPattern = ~/.+@.+\..+/

    WebSpider()
    {
        UrlUtilities.userAgent = USER_AGENT
    }

    void crawl(Receiver receiver, List<Root> roots)
    {
        final Deque<Anchor> stack = new ArrayDeque<>()
        for (Root root in roots)
        {
            stack.add(new Anchor(url:root.url, text:root.name, containerUrl: (String)null, root: root))
        }
        Set<String> visited = new HashSet<>()

        while (!stack.isEmpty())
        {
            final Anchor anchor = stack.removeFirst()

            if (beenThere(anchor.url, visited))
            {   // skip already processed work
                continue
            }

            visited.add(anchor.url.length() <= sha1Length ? anchor.url : EncryptionUtilities.calculateSHA1Hash(anchor.url.bytes))

            try
            {
                if (!anchor.root.isWithinDomain(anchor.url))
                {
                    continue;
                }

                // Snag HTML Document at URL
                Document doc = Jsoup.connect(anchor.url).userAgent(USER_AGENT).followRedirects(true).timeout(10000).get()

                // Fetch anchor tags
                Elements anchors = doc.select("a[href]")
                Set<Anchor> links = []

                for (Element link : anchors)
                {
                    Anchor a = new Anchor([url:link.attr('abs:href'), text:link.text(), containerUrl: doc.location(), root: anchor.root])
                    links.add(a)
                }

                // Tell receiver
                // TODO: Need to implement producer consumer here (don't block producer thread on calling receive)
                receiver.processLink(anchor, doc, System.currentTimeMillis())

                for (Anchor link in links)
                {
                    if (StringUtilities.hasContent(link.url) && !beenThere(link.url, visited))
                    {   // Skip empty or blank urls.
                        stack.add(link)
                    }
                }
            }
            catch (MalformedURLException e)
            {
                processMalformedException(anchor, receiver, e)
            }
            catch (UnsupportedMimeTypeException e)
            {
                processUnsupportedMimeType(e, receiver, anchor)
            }
            catch (HttpStatusException e)
            {
                LOG.info('HTTP status exception (' + e.statusCode + '), url: ' + anchor.url + ', ' + e.message)
            }
            catch (Exception e)
            {
                LOG.info(e.getClass().simpleName + ' occurred. URL: ' + anchor.url + ', msg: ' + e.message)
            }
        }

        println 'Visited ' + visited.size() + ' links.'
    }

    /**
     * Look at mime type and call specific methods on receiver for those, and processOther() for
     * other types.  Theoretically, all could be handled via processOther().  I've just broken a few
     * common types out so that Receiver implements will get the idea on how to handle these.
     *
     * @param e UnsupportedMimeTypeException that was thrown
     * @param receiver Receive that will be passed information regarding the link
     * @param anchor Anchor object representing the link (link text, url, and source doc url)
     */
    private static void processUnsupportedMimeType(UnsupportedMimeTypeException e, Receiver receiver, Anchor anchor)
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
        else if (mimeType.startsWith('application/json'))
        {
            receiver.processJson(anchor, now())
        }
        else
        {
            receiver.processOther(anchor, mimeType, now())
        }
    }

    private static void processMalformedException(Anchor anchor, Receiver receiver, MalformedURLException e)
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

    static boolean beenThere(String url, Set<String> visited)
    {
        return visited.contains(url) || visited.contains(EncryptionUtilities.calculateSHA1Hash(url.bytes))
    }

    static long now()
    {
        System.currentTimeMillis()
    }
}