package com.cedarsoftware.webspider

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class Receiver
{
    void digest(String sourceUrl, String actualUrl, String title, String content, long time)
    {
        println 'source: ' + sourceUrl
        println 'actual: ' + actualUrl
        println 'title: ' + title
//        println 'time: ' + new Date(time)
        println '------------------------------------------------------------------------------------------------------'
//        println content
//        println '******************************************************************************************************'
    }

    void mailto(String emailAddress)
    {
        println 'Email address: ' + emailAddress
    }
}
