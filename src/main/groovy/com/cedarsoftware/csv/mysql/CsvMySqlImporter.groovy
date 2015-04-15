package com.cedarsoftware.csv.mysql

import com.cedarsoftware.util.UniqueIdGenerator
import groovy.transform.CompileStatic
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

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
class CsvMySqlImporter
{
    def importCsv(File file)
    {
        Connection c = getConnection()
        String sql = """\
INSERT INTO business (
id,
company_name,
email,
sic_code,
sic_code6,
naics_code,
contact_name,
first_name,
last_name,
title,
address,
address2,
city,
state,
zip,
county,
phone,
fax,
company_website,
annual_revenue,
employees,
industry,
description,
sic_code_desc,
sic_code6_desc)
 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
"""
        PreparedStatement insert = c.prepareStatement(sql)
        long count = 0
        Map<String, Integer> lengths = [:]

        file.withReader { reader ->
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(reader);
            for (CSVRecord record : records)
            {
                try
                {
                    updateLengths(lengths, record)
                    insert.setLong(1, UniqueIdGenerator.getUniqueId())
                    insert.setString(2, (String) record['Company Name'])
                    insert.setString(3, (String) record['Email'])
                    insert.setInt(4, (int) getLong((String) record['SIC Code']))
                    insert.setLong(5, getLong((String) record['SIC Code 6']))
                    insert.setString(6, (String) record['NAICS Code'])
                    insert.setString(7, (String) record['Contact Name'])
                    insert.setString(8, (String) record['First Name'])
                    insert.setString(9, (String) record['Last Name'])
                    insert.setString(10, (String) record['Title'])
                    insert.setString(11, (String) record['Address'])
                    insert.setString(12, (String) record['Address2'])
                    insert.setString(13, (String) record['City'])
                    insert.setString(14, (String) record['State'])
                    insert.setString(15, (String) record['Zip'])
                    insert.setString(16, (String) record['County'])
                    insert.setString(17, (String) record['Phone'])
                    insert.setString(18, (String) record['Fax'])
                    insert.setString(19, (String) record['Company Website'])
                    long revenue
                    if (record.isMapped('Annual Revenue'))
                    {
                        revenue = getLong((String) record['Annual Revenue'])
                    }
                    else if (record.isMapped('Revenue'))
                    {
                        revenue = getLong((String) record['Revenue'])
                    }
                    else
                    {
                        println 'No revenue column'
                    }
                    insert.setLong(20, revenue)
                    insert.setInt(21, (int) getLong((String) record['Employees']))
                    insert.setString(22, (String) record['Industry'])
                    insert.setString(23, (String) record['Desc'])
                    insert.setString(24, (String) record['SIC Code Description'])
                    insert.setString(25, (String) record['SIC Code6 Description'])
                    insert.addBatch();
                    count++
                    if (count % 35L == 0L)
                    {
                        insert.executeBatch()
                    }
                    if (count % 50000L == 0L)
                    {
                        println count
                    }
                }
                catch (Exception e)
                {
                    println 'error on record ' + count
                    if (record)
                    {
                        println record
                    }
                    e.printStackTrace()
                }
            }
        }
        if (count % 35 != 0)
        {
            insert.executeBatch();
        }
        println 'count = ' + count
        println lengths
        insert.close()
        c.close()
    }

    void updateLengths(LinkedHashMap<String, Integer> lengths, CSVRecord record)
    {
        Map rec = record.toMap()
        Iterator i = rec.entrySet().iterator()
        while (i.hasNext())
        {
            Map.Entry entry = i.next()
            String field = entry.key
            Object value = entry.value
            if (!lengths.containsKey(field))
            {
                lengths[field] = 0i
            }

            if (rec[field] && rec[field] instanceof String)
            {
                if (lengths[field] < rec[field].length())
                {
                    lengths[field] = rec[field].length()
                }
            }
        }
    }

    Connection getConnection()
    {
        try
        {
            return DriverManager.getConnection('jdbc:mysql://127.0.0.1:3306/ncube?autoCommit=true', 'ncube', 'ncube')
        }
        catch (Exception e)
        {
            throw new IllegalStateException('Could not crete connection', e)
        }
    }

    long getLong(String value)
    {
        try
        {
            Long.parseLong(value)
        }
        catch (Exception e)
        {
            return 0L
        }
    }
}