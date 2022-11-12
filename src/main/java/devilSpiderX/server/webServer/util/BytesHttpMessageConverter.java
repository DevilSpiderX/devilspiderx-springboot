package devilSpiderX.server.webServer.util;

import devilSpiderX.server.webServer.langExtend.Bytes;
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
        super(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    protected boolean supports(@NotNull Class<?> clazz) {
        return clazz.equals(Bytes.class);
    }

    @Override
    protected @NotNull Bytes readInternal(@NotNull Class<? extends Bytes> clazz, @NotNull HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        logger.info(clazz);
        InputStream in = inputMessage.getBody();
        Bytes bytes = new Bytes();
        byte[] buffer = new byte[1024 * 64];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount == -1) {
                break;
            }
            bytes.append(buffer, 0, readCount);
        }
        return bytes;
    }

    @Override
    protected void writeInternal(Bytes bytes, @NotNull HttpOutputMessage outputMessage)
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
