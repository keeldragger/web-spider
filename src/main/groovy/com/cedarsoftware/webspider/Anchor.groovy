package com.cedarsoftware.webspider

import org.jsoup.nodes.Document

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class Anchor
{
    Document container
    String url
    String text

    String toString()
    {
        return text + ' (' + url + ')'
    }
}
