package com.cedarsoftware.util.io

import com.cedarsoftware.webspider.Receiver
import com.cedarsoftware.webspider.Root
import com.cedarsoftware.webspider.WebSpider
import groovy.transform.CompileStatic
import org.junit.Test;

/**
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
public class TestSpider
{
    @Test
    void testSpider()
    {
        Root root1 = new Root(name:'MOD Online Backup', url:'http://www.myotherdrive.com/', domain: 'www.myotherdrive.com')
//        Root root2 = new Root(name:'JSON org', url:'http://www.json.org/', domain:'www.json.org')
//        Root root2 = new Root(name:'Government Data', url:'http://www.data.gov/')
        WebSpider spider = new WebSpider()
        Receiver receiver = new Receiver()
        def roots = [root1]
        spider.crawl(receiver, roots)
    }
}
