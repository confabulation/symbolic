package com.github.confabulation.symbolic.utils.io;

import java.io.Closeable;
import java.io.InterruptedIOException;

/**
 * Created by bernard on 19/02/2017.
 */
public interface LineStream extends Closeable {

    String readLine() throws InterruptedIOException;
}
