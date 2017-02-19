package com.github.confabulation.symbolic.utils.io;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;

/**
 * Created by bernard on 19/02/2017.
 */
public class BufferedReader implements LineStream {
    private final BufferedReader bufferedReader;

    public BufferedReader(Reader in) {
        bufferedReader = new BufferedReader(in);
    }

    public String readLine() {
        return bufferedReader.readLine();
    }

    public void close() {
        bufferedReader.close();
    }
}
