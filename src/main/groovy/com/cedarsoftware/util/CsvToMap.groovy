package com.cedarsoftware.util

import groovy.transform.CompileStatic

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
class CsvToMap
{
    static String[] tokenize(String s)
    {
        if (StringUtilities.isEmpty(s))
        {
            return [] as String[]
        }

        String[] tokens = s.split(',')
        for (int i=0; i < tokens.length; i++)
        {
            tokens[i] = removeQuotes(tokens[i])
        }
        return tokens
    }

    static Map<String, String> makeRecord(String[] fields, String[] tokens)
    {
        Map<String, String> record = [:]
        for (String field : fields)
        {
            record[field] = ''
        }
        int len = tokens.length
        for (int i=0; i < len; i++)
        {
            record[fields[i]] = tokens[i]
        }

        return record
    }

    static String removeQuotes(String s)
    {
        if (StringUtilities.isEmpty(s))
        {
            return s
        }
        while (s.startsWith('"') || s.startsWith("'"))
        {
            s = s.substring(1)
        }
        while (s.endsWith('"') || s.endsWith("'"))
        {
            s = s.substring(0, s.length() - 1)
        }

        return s
    }
}
