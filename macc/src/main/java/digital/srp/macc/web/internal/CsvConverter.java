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
package digital.srp.macc.web.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import digital.srp.macc.model.CsvSerializable;

public class CsvConverter extends
        AbstractHttpMessageConverter<List<CsvSerializable>> {
    public static final MediaType MEDIA_TYPE = new MediaType("text", "csv");

    protected static final ArrayList<CsvSerializable> arrayList = new ArrayList<CsvSerializable>();

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(CsvConverter.class);

    public CsvConverter() {
        super(MEDIA_TYPE);
    }

    protected boolean supports(Class<?> clazz) {
        LOGGER.debug(" Test if supports: " + clazz.getName());
        return clazz.isInstance(arrayList);
    }

    protected void writeInternal(List<CsvSerializable> list,
            HttpOutputMessage output) throws IOException,
            HttpMessageNotWritableException {
        output.getHeaders().setContentType(MEDIA_TYPE);
        if (list.size() != 0) {
            CsvSerializable bean0 = list.get(0);
            output.getHeaders().set(
                    "Content-Disposition",
                    "attachment; filename=\""
                            + bean0.getClass().getSimpleName()
                        + ".csv\"");
            OutputStream out = output.getBody();
            Writer writer = new OutputStreamWriter(out);

            writer.write(bean0.toCsvHeader());
            writer.write("\n");

            for (CsvSerializable bean : list) {
                writer.write(bean.toCsv());
                writer.write("\n");
            }

            writer.close();
        }
    }

    @Override
    protected List<CsvSerializable> readInternal(
            Class<? extends List<CsvSerializable>> clazz,
            HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        // TODO Auto-generated method stub
        return null;
    }

}
