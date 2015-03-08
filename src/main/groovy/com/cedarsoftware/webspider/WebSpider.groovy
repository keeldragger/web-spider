package com.cedarsoftware.webspider

import com.cedarsoftware.util.UrlUtilities

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class WebSpider
{
    WebSpider()
    {
    }

    static void main(String[] args)
    {
        WebSpider spider = new WebSpider()
        spider.crawl()
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

            byte[] content = UrlUtilities.getContentFromUrl(url, null, null, true)

            String s = new String(content)
            println url
            println '--------------------------------------------------------------------------------------------------'
            println s
            println '=================================================================================================='
        }
    }
}
