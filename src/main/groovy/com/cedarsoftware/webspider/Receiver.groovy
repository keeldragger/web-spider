package com.cedarsoftware.webspider

import groovy.transform.CompileStatic
import org.jsoup.nodes.Document

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
@CompileStatic
class Receiver
{
    void html(String url, String anchorText, Document doc, long time)
    {
//        println 'source: ' + url
        println 'link: ' + anchorText + ' (' + doc.location() + ')'
        println '------------------------------------------------------------------------------------------------------'
//        println 'title: ' + doc.title()
//        println 'html: ' + doc.outerHtml()
//        println 'time: ' + new Date(time)
    }

    void pdf(String url, String anchorText)
    {
        println '*'
        println '*'
        println 'PDF: ' + anchorText + '(' + url + ')'
        println '*'
        println '*'
    }

    void image(String url, String anchorText, String imageType)
    {
        println '*'
        println '*'
        println 'image: ' + anchorText + '(' + url + ')'
        println 'type: ' + imageType
        println '*'
        println '*'
    }

    void zip(String url, String anchorText)
    {
        println '*'
        println '*'
        println 'zip: ' + anchorText + '(' + url + ')'
        println '*'
        println '*'
    }

    void jar(String url, String anchorText)
    {
        println '*'
        println '*'
        println 'zip: ' + anchorText + '(' + url + ')'
        println '*'
        println '*'
    }

    void mailto(String anchorText, String emailAddress)
    {
        println '*'
        println '*'
        println 'Email: ' + emailAddress
        println '*'
        println '*'
    }
}
