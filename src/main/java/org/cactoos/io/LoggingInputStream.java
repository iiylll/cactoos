/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.io;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;
import org.cactoos.text.FormattedText;

/**
 * Logged input stream.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Fabricio Cabral (fabriciofx@gmail.com)
 * @version $Id$
 * @since 0.12
 */
@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
public final class LoggingInputStream extends InputStream {

    /**
     * The input stream.
     */
    private final InputStream origin;

    /**
     * Where the data comes from.
     */
    private final String source;

    /**
     * The logger.
     */
    private final Logger logger;

    /**
     * Ctor.
     * @param input Source of data
     * @param src The name of source data
     */
    public LoggingInputStream(final InputStream input, final String src) {
        this(input, src, Logger.getLogger(src));
    }

    /**
     * Ctor.
     * @param input Source of data
     * @param src The name of source data
     * @param lgr Message logger
     */
    public LoggingInputStream(final InputStream input, final String src,
        final Logger lgr) {
        super();
        this.origin = input;
        this.source = src;
        this.logger = lgr;
    }

    @Override
    public int read() throws IOException {
        final byte[] buf = new byte[1];
        this.read(buf);
        return Byte.toUnsignedInt(buf[0]);
    }

    @Override
    public int read(final byte[] buf) throws IOException {
        return this.read(buf, 0, buf.length);
    }

    @Override
    public int read(final byte[] buf, final int offset,
        final int len) throws IOException {
        final Instant start = Instant.now();
        int bytes = this.origin.read(buf, offset, len);
        final Instant end = Instant.now();
        if (bytes == -1) {
            bytes = 0;
        }
        this.logger.info(
            new FormattedText(
                "Read %d byte(s) from %s in %dms.",
                bytes,
                this.source,
                Duration.between(start, end).toMillis()
            ).asString()
        );
        return bytes;
    }

    @Override
    public long skip(final long num) throws IOException {
        return this.origin.skip(num);
    }

    @Override
    public int available() throws IOException {
        return this.origin.available();
    }

    @Override
    public void close() throws IOException {
        this.origin.close();
    }

    @Override
    public void mark(final int limit) {
        this.origin.mark(limit);
    }

    @Override
    public void reset() throws IOException {
        this.origin.reset();
    }

    @Override
    public boolean markSupported() {
        return this.origin.markSupported();
    }
}
