/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package digital.srp.finance.model.converters;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.ResourceReader;

public abstract class AbstractCsvParser implements ResourceReader {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractCsvParser.class);

    private Class<?> beanType;

    private PropertyDescriptor[] descriptors;

    private DateFormat dateParser = new SimpleDateFormat("dd-MMM-yy");

    public void setBeanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public List<? extends Object> parseCsv(@NotNull InputStream is)
            throws IOException {
        LOGGER.info("parseCsv");
        List<Object> beans = new ArrayList<Object>();
        LineNumberReader reader = new LineNumberReader(
                new InputStreamReader(is));
        // first line contains field names
        String line1 = reader.readLine();
        if (line1 == null) {
            String msg = "No lines found to parse. First line should contain field names.";
            LOGGER.error(msg);
            throw new IllegalArgumentException("");
        }
        List<String> fieldNameList = parseFieldNames(line1);
        for (String line = reader.readLine(); line != null; line = reader
                .readLine()) {
            beans.add(parseLine(line, fieldNameList));
        }
        return beans;
    }

    public List<String> parseFieldNames(String line1) {
        List<String> fieldNameList = Arrays
                .asList(line1.split(","));
        return fieldNameList;
    }

    protected abstract Object coerce(PropertyDescriptor descriptor, Object val);

    private PropertyDescriptor[] getPropertyDescriptors()
            throws IntrospectionException {
        if (descriptors == null) {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanType);
            descriptors = beanInfo.getPropertyDescriptors();
        }
        return descriptors;
    }

    private String[] parseCsvLine(String line) {
        Pattern p = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        String[] fields = p.split(line);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].replace("\"", "");
        }
        return fields;
    }

    public Object parseLine(String line, List<String> fieldNameList) {
        try {
            String[] fields = parseCsvLine(line);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("  fields found: %1$s",
                        Arrays.asList(fields)));
            }
            Object bean = beanType.newInstance();
            for (PropertyDescriptor descriptor : getPropertyDescriptors()) {
                if (fieldNameList.contains(descriptor.getName())) {
                    Method writeMethod = descriptor.getWriteMethod();
                    try {
                        int idx = fieldNameList.indexOf(descriptor.getName());
                        Object val = fields[idx].trim();
                        LOGGER.debug(String
                                .format("  setting field: %1$s to '%2$s':%3$s (idx:%4$d)",
                                        descriptor.getName(), val, val
                                                .getClass().getName(), idx));
                        if (((String) val).length() == 0
                                || "-".equals((String) val)) {
                            val = null;
                        } else {
                            val = coerce(descriptor, val);
                        }
                        writeMethod.invoke(bean, val);
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        LOGGER.error(String.format("  %1$s: %2$s",
                                e.getMessage(), bean), e);
                    }
                }
            }
            return bean;
        } catch (Exception e) {
            LOGGER.error(String.format("  Unable to load line %1$s", line));
            LOGGER.error(String.format("  %1$s: ", e.getMessage()), e);
            return null;
        }
    }

    public Date parseDate(String string) throws ParseException {
        return dateParser.parse(string);
    }

    @Override
    public Object readFrom(Resource resource, ClassLoader classLoader)
            throws Exception {
        return parseCsv(resource.getInputStream());
    }
}
