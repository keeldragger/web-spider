web-spider
==========

Spider a list of URLs.  This library will drill through the URLs, using multiple threads, streaming back pages of content. To include in your project:
```
<dependency>
  <groupId>com.cedarsoftware</groupId>
  <artifactId>web-spider</artifactId>
  <version>1.0.0</version>
</dependency>
```
[Donations welcome](https://coinbase.com/jdereg)

**web-spider** consists of one main class, `WebSpider`.  Create a listener, passed it to the WebSpider, and your code will start receiving URLs and content.  From there, you can dump them into Mongo, Hadoop, MySQL, whatever.  Timestamp the content, index the content, and so forth.

Usage
-----
WebSpider spider = new WebSpider(['url1', 'url2', ...], n)  // n = number of threads to use
SpiderListener listener = new SpiderListener()
spider.crawl(listener)

History
 * 1.0.0
   * Initial version

by John DeRegnaucourt
