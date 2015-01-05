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

package module;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Wesley Lin on 11/29/14.
 */
public class AndroidString {
    private String key;
    private String value;

    public AndroidString(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public AndroidString(AndroidString androidString) {
        this.key = androidString.getKey();
        this.value = androidString.getValue();
    }

    public AndroidString() {
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "<string name=\"" +
                key +
                "\">" +
                value +
                "</string>";
    }

    private static final String KEY_STRING = "</string>";
    private static final String SPLIT_KEY = "<string";
    private static final String KEY_START = "name=\"";
    private static final String KEY_END = "\">";
    private static final String VALUE_END = "</string>";

    /** @deprecated */
    public static List<AndroidString> getAndroidStringsList(InputStream xml) {
        List<AndroidString> result = new ArrayList<AndroidString>();

        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(xml);

            AndroidString androidString = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if (startElement.getName().getLocalPart().equals("string")) {
                        androidString = new AndroidString();

                        Iterator<Attribute> attributes = startElement.getAttributes();

                        while (attributes.hasNext()) {
                            Attribute attribute = attributes.next();
                            if (attribute.getName().toString().equals("name")) {
                                androidString.setKey(attribute.getValue());
                            }
                        }

                        event = eventReader.nextEvent();
                        String value = event.asCharacters().getData().trim();

                        // todo: if the value starts with xml tags(<u>), the value will be empty
                        androidString.setValue(value);
                        continue;
                    }
                }

                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals("string")) {
                        result.add(androidString);
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<AndroidString> getAndroidStringsList(byte[] xmlContentByte) {
        try {
            String fileContent = new String(xmlContentByte, "UTF-8");

            if (!fileContent.contains(KEY_STRING))
                return null;

            String[] tokens = fileContent.split(SPLIT_KEY);

            List<AndroidString> result = new ArrayList<AndroidString>();

            for (int i = 0; i < tokens.length; i++) {

                if (tokens[i].contains(KEY_STRING)) {

                    int keyStartIndex = tokens[i].indexOf(KEY_START) + KEY_START.length();
                    int keyEndIndex = tokens[i].indexOf(KEY_END);
                    int valueEndIndex = tokens[i].indexOf(VALUE_END);

                    if (keyStartIndex >= tokens[i].length()
                            || keyEndIndex >= tokens[i].length()
                            || (keyEndIndex + KEY_END.length()) >= tokens[i].length()
                            || valueEndIndex >= tokens[i].length()) {
                        continue;
                    }

                    String key = tokens[i].substring(keyStartIndex, keyEndIndex).trim();
                    String value = tokens[i].substring(keyEndIndex + KEY_END.length(), valueEndIndex).trim();

                    result.add(new AndroidString(key, value));
                }
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAndroidStringKeys(List<AndroidString> list) {
        List<String> result = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }

    public static List<String> getAndroidStringValues(List<AndroidString> list) {
        List<String> result = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i).getValue());
        }
        return result;
    }
}
