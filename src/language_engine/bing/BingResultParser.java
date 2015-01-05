/*
 * Copyright 2014-2015 Wesley Lin
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
 */

package language_engine.bing;

import com.intellij.openapi.util.io.StreamUtil;
import sun.nio.cs.StandardCharsets;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Wesley Lin on 12/5/14.
 */
public class BingResultParser {
    static final String TranslateArrayResponse = "TranslateArrayResponse";
    static final String From = "From";
    static final String TranslatedText = "TranslatedText";

    public static List<TranslateArrayResponse> parseTranslateArrayResponse(String xml) {

        InputStream stream = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));

        List<TranslateArrayResponse> result = new ArrayList<TranslateArrayResponse>();

        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(stream);

            TranslateArrayResponse translateArrayResponse = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if (startElement.getName().getLocalPart().equals(TranslateArrayResponse)) {
                        translateArrayResponse = new TranslateArrayResponse();
                    }
                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals(From)) {
                            event = eventReader.nextEvent();
                            translateArrayResponse.setFrom(event.asCharacters().getData());
                            continue;
                        }
                    }
                    if (event.asStartElement().getName().getLocalPart().equals(TranslatedText)) {
                        event = eventReader.nextEvent();
                        translateArrayResponse.setTranslatedText(event.asCharacters().getData());
                        continue;
                    }
                }

                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(TranslateArrayResponse)) {
                        result.add(translateArrayResponse);
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        return result;
    }
}
