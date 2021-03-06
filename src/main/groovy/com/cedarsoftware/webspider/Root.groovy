package com.cedarsoftware.webspider

import com.cedarsoftware.util.StringUtilities

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * This class is used as input to the WebSpider
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

class Root
{
    String name
    String url
    String domain
    private static final Pattern protocolPattern = ~/(http:\\/\\/|https:\\/\\/|ftp:\\/\\/|mailto:)(.+)/

    boolean isWithinDomain(String url)
    {
        if (StringUtilities.hasContent(domain))
        {
            Matcher matcher = protocolPattern.matcher(url);
            return matcher && matcher.group(2).toLowerCase().startsWith(domain)
        }
    }
}
