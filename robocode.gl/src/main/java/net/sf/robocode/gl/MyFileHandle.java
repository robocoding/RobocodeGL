package net.sf.robocode.gl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.FileInputStream;
import java.io.InputStream;

public final class MyFileHandle extends FileHandle {
	public MyFileHandle(String fileName, Files.FileType type) {
		super(fileName, type);
	}

	@Override
	public final InputStream read() {
		if (type == Files.FileType.Classpath || (type == Files.FileType.Internal && !file().exists())
			|| (type == Files.FileType.Local && !file().exists())) {
			InputStream input = MyFileHandle.class.getResourceAsStream("/" + file.getPath().replace('\\', '/'));
			if (input == null) throw new GdxRuntimeException("MyFile not found: " + file + " (" + type + ")");
			return input;
		}
		try {
			return new FileInputStream(file());
		} catch (Exception ex) {
			if (file().isDirectory())
				throw new GdxRuntimeException("MyCannot open a stream to a directory: " + file + " (" + type + ")", ex);
			throw new GdxRuntimeException("MyError reading file: " + file + " (" + type + ")", ex);
		}
	}
}
