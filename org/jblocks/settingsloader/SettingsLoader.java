package org.jblocks.settingsloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SettingsLoader {

	public SettingsLoader(File settings) throws IOException {
		// does the settings file exist?
		if (settings.exists()) {

			// can we read the file?
			if (settings.canRead()) {

			} else {
				throw new IOException("settings file is not readable: "
						+ settings.getAbsolutePath());
			}
		} else {
			throw new FileNotFoundException("settings file not found :"
					+ settings.getAbsolutePath());
		}

	}
}
