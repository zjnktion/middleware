package cn.zjnktion.middleware.zson.writer;

import java.io.IOException;

/**
 * @author zjnktion
 */
public interface ZsonWriter {

    void write(boolean bool) throws IOException;

    void write(byte b) throws IOException;

    void write(char c) throws IOException;

    void write(short s) throws IOException;

    void write(int i) throws IOException;

    void write(long l) throws IOException;

    void write(double d) throws IOException;

    void write(char[] cbuf) throws IOException;

    void write(char[] cbuf, int off, int len) throws IOException;

    void write(String str) throws IOException;

    void write(String str, int off, int len) throws IOException;

    void writeNull() throws IOException;

    String toString();
}
