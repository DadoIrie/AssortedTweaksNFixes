package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import gg.moonflower.etched.api.util.Mp3InputStream;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;

@Mixin(Mp3InputStream.class)
public abstract class Mp3InputStreamMixin {

    @Unique
    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(Mp3InputStreamMixin.class);
    @Unique
    private static final int MAX_CONSECUTIVE_BAD_FRAMES = 50;

    @Shadow private Bitstream stream;
    @Shadow private Decoder decoder;
    @Shadow private ByteBuffer buffer;
    @Shadow private AudioFormat format;

    /**
     * @author Damir Krupic
     * @reason Vanilla behavior aborts the entire read on a single corrupt MP3 frame - a normal, expected
     * occurrence in live network streaming - discarding an entire buffer's worth of otherwise-good audio and
     * producing an audible scratch/skip. Skips just the bad frame and resyncs to the next one instead.
     */
    @Overwrite
    private boolean fillBuffer() throws IOException {
        this.buffer.clear();

        for (int badFrames = 0; badFrames < MAX_CONSECUTIVE_BAD_FRAMES; badFrames++) {
            try {
                Header header = this.stream.readFrame();
                if (header == null) { // EOF
                    this.buffer.flip();
                    return true;
                }

                if (this.format == null) {
                    int channels = header.mode() == Header.SINGLE_CHANNEL ? 1 : 2;
                    this.format = new AudioFormat(header.frequency(), Short.SIZE, channels, true, false);
                }

                SampleBuffer decoderOutput = (SampleBuffer) this.decoder.decodeFrame(header, this.stream);
                short[] data = decoderOutput.getBuffer();
                this.buffer.asShortBuffer().put(data);
                this.buffer.position(data.length * Short.BYTES);
                this.buffer.flip();
                return false;
            } catch (Throwable t) {
                if (badFrames == 0) {
                    LOGGER.debug("Skipping corrupt MP3 frame", t);
                }
            } finally {
                this.stream.closeFrame();
            }
        }

        throw new IOException("Too many consecutive corrupt MP3 frames");
    }
}