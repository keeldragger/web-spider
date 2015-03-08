package com.cedarsoftware.webspider

import com.cedarsoftware.util.StringUtilities
import com.cedarsoftware.util.UrlUtilities
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class WebSpider
{
    static final String USER_AGENT = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
    private static final Logger LOG = LogManager.getLogger(WebSpider.class);

    WebSpider()
    {
        UrlUtilities.setUserAgent(USER_AGENT);
    }

    void crawl()
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

            String content = fetch(url)
            List<String> urls = extractUrls(content)
            stack.addAll(urls)
        }
    }

    String fetch(String url)
    {
        byte[] content = UrlUtilities.getContentFromUrl(url, null, null, true)
        if (content == null)
        {
            return ''
        }

        try
        {
            return StringUtilities.createString(content, 'UTF-8')
        }
        catch (Exception e)
        {
            LOG.warn('Error creating string from content, url: ' + url, e)
            return ''
        }
    }

    List<String> extractUrls(String content)
    {

    }
}
