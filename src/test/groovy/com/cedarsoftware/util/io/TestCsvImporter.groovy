package com.cedarsoftware.util.io

import com.cedarsoftware.csv.mongo.CsvImporter
import com.cedarsoftware.csv.mysql.CsvMySqlImporter
import org.junit.Ignore
import org.junit.Test

import static groovy.io.FileType.FILES

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
class TestCsvImporter
{
    @Ignore
    void testImport()
    {
        CsvImporter importer = new CsvImporter()
        new File('/Users/jderegnaucourt/Desktop/Personal/data/').eachFileRecurse(FILES)
                { file ->
                    if (file.name.matches(~/^.*\.csv$/))
                    {
                        importer.importCsv(file)
                    }
                }
    }

    @Test
    void testImportMysql()
    {
        CsvMySqlImporter importer = new CsvMySqlImporter()
        new File('/Users/jderegnaucourt/Desktop/Personal/data/').eachFileRecurse(FILES)
                { file ->
                    if (file.name.matches(~/^.*\.csv$/))
                    {
                        println file
                        importer.importCsv(file)
                    }
                }
    }
}
