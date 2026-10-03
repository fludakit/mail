package io.github.fludakit.mail;

import jakarta.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

/**
 * Represents an email attachment.
 */
public class MailAttachment {
    private final String filename;
    private final String contentType;
    private final Supplier<InputStream> streamSupplier;

    public MailAttachment(String filename, String contentType, Supplier<InputStream> streamSupplier) {
        this.filename = filename;
        this.contentType = contentType;
        this.streamSupplier = streamSupplier;
    }

    public String getFilename() { 
        return filename; 
    }

    public DataSource toDataSource() {
        return new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                InputStream is = streamSupplier.get();
                if (is == null) throw new IOException("Attachment stream supplier returned null for " + filename);
                return is;
            }
            @Override
            public OutputStream getOutputStream() { 
                throw new UnsupportedOperationException("Read-only"); 
            }
            @Override
            public String getContentType() { 
                return contentType != null ? contentType : "application/octet-stream"; 
            }
            @Override
            public String getName() { 
                return filename; 
            }
        };
    }
}
