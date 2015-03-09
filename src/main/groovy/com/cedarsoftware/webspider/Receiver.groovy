package com.cedarsoftware.webspider

import groovy.transform.CompileStatic
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
@CompileStatic
class Receiver
{
    /**
     * @param anchor Contains link text, url, and pointer to document containing link
     * @param doc Document that this anchor points to
     * @param time When the content (doc) was retrieved.
     */
    void processLink(Anchor anchor, Document doc, long time)
    {
//        println 'source: ' + url
        println 'link: ' + anchor
        println 'source doc: ' + anchor.container
        println '------------------------------------------------------------------------------------------------------'
//        println 'title: ' + doc.title()
//        println 'html: ' + doc.outerHtml()
//        println 'time: ' + new Date(time)

    }

    void processPDF(Anchor anchor, long time)
    {
        println 'PDF: ' + anchor
        println 'found in: ' + anchor.container
        println '------------------------------------------------------------------------------------------------------'
    }

    void processImage(Anchor anchor, String imageType, long time)
    {
        println 'image: ' + anchor
        println 'type: ' + imageType
        println 'found in: ' + anchor.container
        println '------------------------------------------------------------------------------------------------------'
    }

    void processZip(Anchor anchor, long time)
    {
        println 'zip: ' + anchor
        println 'found in: ' + anchor.container
        println '------------------------------------------------------------------------------------------------------'
    }

    void processJar(Anchor anchor, long time)
    {
        println 'jar: ' + anchor
        println 'found in: ' + anchor.container
        println '------------------------------------------------------------------------------------------------------'
    }

    void processMailto(Anchor anchor, String emailAddress, long time)
    {
        println 'email: ' + anchor
        println 'address: ' + emailAddress
        println 'found in: ' + anchor.container
        println '------------------------------------------------------------------------------------------------------'
    }

    void processOther(Anchor anchor, String mimeType, long time)
    {
        println 'image: ' + anchor
        println 'mimeType: ' + mimeType
        println 'found in: ' + anchor.container
        println '------------------------------------------------------------------------------------------------------'
    }

    /**
     * Call this method if you want to retrieve all the CSS links (URLs) from the
     * passed in Document.
     * @param doc Document that was received
     * @return Set<String> URLs to CSS content
     */
    private Elements fetchLinks(Document doc)
    {
        Elements imports = doc.select("link[href]")

        // Example of getting content from the element
//        for (Element link : imports)
//        {
//            String tagName = link.tagName()
//            String url1 = link.attr('abs:href')
//            String rel = link.attr('rel')
//        }

        return imports
    }

    private Elements fetchMedia(Document doc)
    {
        Elements media = doc.select('[src]')

        // Example of getting content from the element
//        for (Element src : media)
//        {
//            final String tagName = src.tagName()
//            if (tagName.equals('img'))
//            {
//                String url = src.attr('abs:src')
//                String width = src.attr('width')
//                String height = src.attr('height')
//                String altText = src.attr('alt')
//            }
//            else
//            {
//                String url = src.attr('abs:src')
//            }
//        }
        return media
    }
}
