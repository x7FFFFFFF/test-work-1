package com.noname.options.server;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.noname.server.Codec;
import com.noname.server.Extras;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLStreamConstants;
import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

public class XmlClientCodec implements Codec {
    private static final String EXTRA = "extra";
    private static final String REQUEST_TYPE = "request-type";

    private static final AsyncXMLInputFactory XML_INPUT_FACTORY = new InputFactoryImpl();

    private static final String RESULT_CODE = "result-code";
    private static final String EXTRA_NAME = "name";
    private static final String REQUEST = "request";

    private final AsyncXMLStreamReader<AsyncByteArrayFeeder> streamReader = XML_INPUT_FACTORY.createAsyncForByteArray();
    private final AsyncByteArrayFeeder streamFeeder = (AsyncByteArrayFeeder) streamReader.getInputFeeder();

    private static final XMLOutputFactory2 XML_OUTPUT_FACTORY = new OutputFactoryImpl();


    @Override
    public Object decode(byte[] buffer) throws Exception {
        String tagName = null;
        String extraKey = null;
        TestResponse result = new TestResponse();
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
                            case RESULT_CODE:
                                result.setResultCode(Integer.parseInt(streamReader.getText()));
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
        TestRequest request = (TestRequest)obj;
        final EnumMap<Extras, String> extras = request.getExtras();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            XMLStreamWriter2 xmlStreamWriter = (XMLStreamWriter2) XML_OUTPUT_FACTORY.createXMLStreamWriter(bos);
            xmlStreamWriter.writeStartDocument();
            xmlStreamWriter.writeStartElement(REQUEST);
            xmlStreamWriter.writeStartElement(REQUEST_TYPE);
            xmlStreamWriter.writeCharacters(request.getRequestsType().getTagText());
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
