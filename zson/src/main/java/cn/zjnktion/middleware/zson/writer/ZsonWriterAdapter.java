package cn.zjnktion.middleware.zson.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * @author zjnktion
 */
public class ZsonWriterAdapter extends Writer implements ZsonWriter {

    private static final int DEFAULT_BUFFER_SIZE = 1 << 5;

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    private char[] buffer;

    private int count;

    public ZsonWriterAdapter() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public ZsonWriterAdapter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("ZsonWriter initial char buffer size can not less than 0.");
        }
        this.buffer = new char[initialSize];
    }

    public void write(boolean bool) throws IOException {
        write(bool ? "true" : "false");
    }

    public void write(byte b) throws IOException {
        write((char) b);
    }

    public void write(char c) throws IOException {
        synchronized (lock) {
            int newCount = count + 1;
            if (newCount > MAXIMUM_CAPACITY) {
                throw new IllegalStateException("ZsonWriter can not expand char buffer size more than " + MAXIMUM_CAPACITY);
            }
            if (newCount > buffer.length) {
                int newSize = powOfTwo(newCount);
                buffer = Arrays.copyOf(buffer, newSize);
            }
            buffer[count] = c;
            count = newCount;
        }
    }

    public void write(short s) throws IOException {
        write(Short.toString(s));
    }

    public void write(int i) throws IOException {
        write(Integer.toString(i));
    }

    public void write(long l) throws IOException {
        write(Long.toString(l));
    }

    public void write(double d) throws IOException {
        write(Double.toString(d));
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || (off + len) < 0 || (off + len) > cbuf.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }

        synchronized (lock) {
            int newCount = count + len;
            if (newCount > MAXIMUM_CAPACITY) {
                throw new IllegalStateException("ZsonWriter can not expand char buffer size more than " + MAXIMUM_CAPACITY);
            }
            if (newCount > buffer.length) {
                int newSize = powOfTwo(newCount);
                buffer = Arrays.copyOf(buffer, newSize);
            }
            System.arraycopy(cbuf, off, buffer, count, len);
            count = newCount;
        }
    }

    public void writeNull() throws IOException {
        write("null");
    }

    public String toString() {
        synchronized (lock) {
            return new String(buffer, 0, count);
        }
    }

    public void flush() throws IOException {
        // do nothing
    }

    public void close() throws IOException {
        // do nothing
    }

    private int powOfTwo(int num) {
        int n = num - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
