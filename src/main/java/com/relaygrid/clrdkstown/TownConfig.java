package com.relaygrid.clrdkstown;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

public class TownConfig extends YamlConfiguration {
	protected static final Logger LOGGER = Logger.getLogger("CLRDKSTown");
	protected static final Charset UTF8 = StandardCharsets.UTF_8;
	protected final File configFile;
	private final AtomicInteger pendingDiskWrites = new AtomicInteger(0);
	private final byte[] bytebuffer = new byte[1024];
	
	public TownConfig(final File configFile) {
		super();
		this.configFile = configFile.getAbsoluteFile();
	}
	
	public synchronized void load() {
        if (pendingDiskWrites.get() != 0) {
            LOGGER.log(Level.INFO, "File {0} not read, because it''s not yet written to disk.", configFile);
            return;
        }
        if (!configFile.getParentFile().exists()) {
        	if (!configFile.getParentFile().mkdirs()) {
        		LOGGER.log(Level.SEVERE, "Could not make config file!");
        	}
        }
        
        if (!configFile.exists()) {
        	//create config file from template
        }
        
        try {
        	try (final FileInputStream inputStream = new FileInputStream(configFile)) {
        		final long startSize = configFile.length();
        		if (startSize > Integer.MAX_VALUE) {
        			throw new Exception("File too big.");
        		}
        		ByteBuffer buffer = ByteBuffer.allocate((int) startSize);
        		int length;
        		while ((length = inputStream.read(bytebuffer)) != -1) {
        			if (length > buffer.remaining()) {
        				final ByteBuffer resize = ByteBuffer.allocate(buffer.capacity() + length - buffer.remaining());
        				final int resizePosition = buffer.position();
        				((Buffer) buffer).rewind();
        				resize.put(buffer);
        				resize.position(resizePosition);
        				buffer = resize;
        			}
        			buffer.put(bytebuffer, 0, length);
        		}
        		((Buffer) buffer).rewind();
        		final CharBuffer data = CharBuffer.allocate(buffer.capacity());
        		CharsetDecoder decoder = UTF8.newDecoder();
        		CoderResult result = decoder.decode(buffer, data, true);
        		if (result.isError()) {
        			throw new Exception("Config file must be UTF8 encoded! Your config may have invalid characters.");
        		} else {
        			decoder.flush(data);
        		}
        		final int end = data.position();
        		((Buffer) data).rewind();
        		super.loadFromString(data.subSequence(0, end).toString());
        	}
        } catch (final IOException ex) {
        	LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (final Exception ex) {
        	LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
	}
	
}
