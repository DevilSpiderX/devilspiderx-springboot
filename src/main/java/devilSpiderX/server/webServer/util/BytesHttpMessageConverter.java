package devilSpiderX.server.webServer.util;

import devilSpiderX.server.webServer.lang.Bytes;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BytesHttpMessageConverter extends AbstractHttpMessageConverter<Bytes> {

    public BytesHttpMessageConverter() {
        super(MediaType.ALL);
    }

    @Override
    protected boolean supports(@NotNull Class<?> clazz) {
        return true;
    }

    @Override
    protected @NotNull Bytes readInternal(@NotNull Class<? extends Bytes> clazz, @NotNull HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        logger.info(clazz);
        InputStream in = inputMessage.getBody();
        Bytes bytes = new Bytes();
        int offset = 0;
        while (true) {
            byte[] buffer = new byte[1024 * 64];
            int readCount = in.read(buffer, offset, buffer.length);
            bytes.append(buffer);
            if (readCount == -1) {
                break;
            }
            offset += readCount;
        }
        return bytes;
    }

    @Override
    protected void writeInternal(Bytes bytes, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType(new MediaType("text", "html"));
        headers.setContentLength(bytes.length());
        out.write(bytes.toByteArray());
        out.writeTo(outputMessage.getBody());
        out.close();
    }
}
