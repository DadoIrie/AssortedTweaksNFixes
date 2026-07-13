package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import gg.moonflower.etched.api.util.AsyncInputStream;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Mixin(AsyncInputStream.class)
public abstract class AsyncInputStreamMixin {

    @Unique
    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(AsyncInputStreamMixin.class);
    @Unique
    private static final int MAX_RETRIES = 5;
    @Unique
    private static final long RETRY_DELAY_MILLIS = 2000L;

    @Shadow @Final private List<byte[]> readBytes;
    @Shadow private volatile boolean closed;
    @Shadow private void appendBuffer(byte[] data) { throw new AssertionError(); }

    /**
     * @author Damir Krupic
     * @reason Vanilla behavior permanently fails on a single mid-stream network error with no reconnect attempt,
     * causing repeated sound engine crashes. Adds retry-with-backoff before giving up, reusing the existing
     * exception-propagation path so downstream failure handling (including FAILED_URLS registration) is untouched.
     */
    @Overwrite
    private void lambda$new$0(AsyncInputStream.InputStreamSupplier source, int bufferSize, CompletableFuture<?> initialWait, int buffers) {
        int retryCount = 0;

        while (!this.closed) {
            try (InputStream stream = source.open()) {
                while (!this.closed) {
                    byte[] buffer = new byte[bufferSize];
                    int read, byteCount = 0;
                    while (!this.closed && byteCount < buffer.length && (read = stream.read(buffer, byteCount, buffer.length - byteCount)) != -1) {
                        byteCount += read;
                    }

                    if (!this.closed && byteCount > 0) {
                        if (byteCount < buffer.length) {
                            byte[] newBuffer = new byte[byteCount];
                            System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
                            this.appendBuffer(newBuffer);
                        } else {
                            this.appendBuffer(buffer);
                        }
                        retryCount = 0;
                    }

                    if (!initialWait.isDone() && (this.closed || this.readBytes.size() >= buffers)) {
                        initialWait.complete(null);
                    }
                }
            } catch (IOException e) {
                if (!initialWait.isDone()) {
                    this.closed = true;
                    initialWait.completeExceptionally(e);
                    throw new CompletionException(e);
                }

                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    LOGGER.warn("Stream failed after {} retries, stopping playback", MAX_RETRIES, e);
                    this.closed = true;
                    // Return normally instead of throwing: readFuture completes successfully, so every
                    // subsequent read() falls through to the normal EOF path instead of rethrowing this
                    // exception forever - which is what was driving the sound engine into a crash loop.
                    return;
                }

                LOGGER.warn("Stream read failed, retrying ({}/{}) in {} seconds", retryCount, MAX_RETRIES, RETRY_DELAY_MILLIS / 1000, e);

                try {
                    Thread.sleep(RETRY_DELAY_MILLIS);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    this.closed = true;
                    return;
                }
            }
        }
    }
}