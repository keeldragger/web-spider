package com.cedarsoftware.webspider
/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class Anchor
{
    String container
    String url
    String text

    String toString()
    {
        return text + ' (' + url + ')'
    }
}
