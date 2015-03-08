package com.cedarsoftware.webspider

import com.cedarsoftware.util.EncryptionUtilities

/**
 * Created by jderegnaucourt on 2015/03/08.
 */
class Content
{
    String url
    byte[] content
    Date date

    Content(String url)
    {
        if (!url)
        {
            throw new SpiderException("Url cannot be empty")
        }
    }

    String sha1Url()
    {
        return EncryptionUtilities.calculateSHA1Hash(url.getBytes())
    }

    String sha1Content()
    {
        if (!content)
        {
            return EncryptionUtilities.calculateSHA1Hash([] as byte)
        }
        return EncryptionUtilities.calculateSHA1Hash(content)
    }
}
