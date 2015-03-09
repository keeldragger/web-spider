package com.cedarsoftware.webspider

import groovy.transform.CompileStatic
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

/**
 * This class is the base class to be used by all Receivers.  Subclass this, and pass in
 * your receiver to the WebSpider.  It will call your receiver frequently while processing
 * the source URLs.
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
        println 'source doc: ' + anchor.containerUrl
//        println doc.outerHtml()
        println '------------------------------------------------------------------------------------------------------'
//        println 'title: ' + doc.title()
//        println 'time: ' + new Date(time)

    }

    void processPDF(Anchor anchor, long time)
    {
        println 'PDF: ' + anchor
        println 'found in: ' + anchor.containerUrl
        println '------------------------------------------------------------------------------------------------------'
    }

    void processImage(Anchor anchor, String imageType, long time)
    {
        println 'image: ' + anchor
        println 'type: ' + imageType
        println 'found in: ' + anchor.containerUrl
        println '------------------------------------------------------------------------------------------------------'
    }

    void processZip(Anchor anchor, long time)
    {
        println 'zip: ' + anchor
        println 'found in: ' + anchor.containerUrl
        println '------------------------------------------------------------------------------------------------------'
    }

    void processJar(Anchor anchor, long time)
    {
        println 'jar: ' + anchor
        println 'found in: ' + anchor.containerUrl
        println '------------------------------------------------------------------------------------------------------'
    }

    void processMailto(Anchor anchor, String emailAddress, long time)
    {
        println 'email: ' + anchor
        println 'address: ' + emailAddress
        println 'found in: ' + anchor.containerUrl
        println '------------------------------------------------------------------------------------------------------'
    }

    void processJson(Anchor anchor, long time)
    {

    }

    void processOther(Anchor anchor, String mimeType, long time)
    {
        println 'image: ' + anchor
        println 'mimeType: ' + mimeType
        println 'found in: ' + anchor.containerUrl
        println '------------------------------------------------------------------------------------------------------'
    }

    /**
     * Call this method if you want to retrieve all the CSS links (URLs) from the
     * passed in Document.
     * @param doc Document that was received
     * @return Set<String> URLs to CSS content
     */
    protected Elements fetchLinks(Document doc)
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

    protected Elements fetchMedia(Document doc)
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
