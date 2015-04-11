package com.cedarsoftware.csv.mongo

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper
import groovy.transform.CompileStatic
import org.bson.Document

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
class CsvImporter
{
    def importCsv(File file)
    {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 )
        MongoDatabase database = mongoClient.getDatabase("us_biz")
        MongoCollection<Document> collection = database.getCollection("companies")
        int recNum = 0
        file.withReader { reader ->
            Iterator i = CsvParser.parseCsv([autoDetect:true], reader)
            while (i.hasNext())
            {
                PropertyMapper mapper = (PropertyMapper) i.next()
                recNum++
                try
                {
                    Map record = toMap(mapper)
                    Document business = new Document(record)
                    collection.insertOne(business)
                }
                catch (Exception e)
                {
                    println 'error on record ' + recNum
                    println e.message
                }
            }
        }
    }

    Map toMap(PropertyMapper mapper)
    {
        Map ret = [:]
        Map cols = (Map) mapper.columns;
        for (Map.Entry<String, Object> entry : cols.entrySet())
        {
            Integer i = (Integer) entry.value
            ret[entry.key] = mapper.getAt(i)
        }
        return ret
    }
}
