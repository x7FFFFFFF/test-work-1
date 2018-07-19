package com.noname.server;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.XMLStreamConstants;
import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

public class TestXmlServerCodec implements ICodec {
    private static final String EXTRA = "extra";
    private static final String REQUEST_TYPE = "request-type";

    private static final AsyncXMLInputFactory XML_INPUT_FACTORY = new InputFactoryImpl();
    public static final String RESPONSE = "response";
    public static final String RESULT_CODE = "result-code";
    public static final String EXTRA_NAME = "name";
    private final AsyncXMLStreamReader<AsyncByteArrayFeeder> streamReader = XML_INPUT_FACTORY.createAsyncForByteArray();
    private final AsyncByteArrayFeeder streamFeeder = (AsyncByteArrayFeeder) streamReader.getInputFeeder();

    private static final XMLOutputFactory2 XML_OUTPUT_FACTORY = new OutputFactoryImpl();


    @Override
    public Object decode(byte[] buffer) throws Exception {
        String tagName = null;
        String extraKey = null;
        TestRequest result = new TestRequest();
        final EnumMap<Extras, String> extras = result.getExtras();
        streamFeeder.feedInput(buffer, 0, buffer.length);
        while (!streamFeeder.needMoreInput()) {
            int type = streamReader.next();
            switch (type) {
                case XMLStreamConstants.START_ELEMENT:
                    tagName = streamReader.getLocalName();
                    if (tagName.equals(EXTRA)) {
                        extraKey = streamReader.getAttributeValue(0);
                    }

                    break;
                case XMLStreamConstants.CHARACTERS:
                    // out.add(new XmlCharacters(streamReader.getText()));
                    if (tagName != null) {
                        switch (tagName) {
                            case EXTRA:
                                if (extraKey != null) {
                                    extras.put(Extras.value(extraKey), streamReader.getText());
                                }
                                break;
                            case REQUEST_TYPE:
                                result.setRequestsType(RequestsTypes.value(streamReader.getText()));
                                break;
                        }
                    }
                    break;
            }
        }

        return result;
    }

    @Override
    public byte[] encode(Object obj) throws Exception {
        TestResponse response = (TestResponse)obj;
        final EnumMap<Extras, String> extras = response.getExtras();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            XMLStreamWriter2 xmlStreamWriter = (XMLStreamWriter2) XML_OUTPUT_FACTORY.createXMLStreamWriter(bos);
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeStartElement(RESPONSE);
                xmlStreamWriter.writeStartElement(RESULT_CODE);
                    xmlStreamWriter.writeCharacters(String.valueOf(response.getResultCode()));
                xmlStreamWriter.writeEndElement();
                for (Map.Entry<Extras, String> entry : extras.entrySet()) {
                    xmlStreamWriter.writeStartElement(EXTRA);
                    xmlStreamWriter.writeAttribute(EXTRA_NAME, entry.getKey().getAttrName() );
                    xmlStreamWriter.writeCharacters(entry.getValue());
                    xmlStreamWriter.writeEndElement();
                }
            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndDocument();
            return bos.toByteArray();
        }
    }
}