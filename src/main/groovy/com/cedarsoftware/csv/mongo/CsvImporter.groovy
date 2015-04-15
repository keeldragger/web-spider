package com.cedarsoftware.csv.mongo

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import groovy.transform.CompileStatic
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
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
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(reader);
            for (CSVRecord record : records)
            {
                recNum++
                try
                {
                    Map map = record.toMap()
                    Document business = new Document((Map<String, Object>)map)
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
}
