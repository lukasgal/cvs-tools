package org.gal.tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class LineMatcher {
	
	public Map<Integer, String> find(final String p_fileName, final String p_pattern) {
		Map<Integer, String> l_match = new TreeMap<>();
		Pattern regexp = Pattern.compile(p_pattern);
		Matcher matcher = regexp.matcher("");

		Path path = Paths.get(p_fileName);
		try (BufferedReader reader = Files.newBufferedReader(path, ENCODING);
			      LineNumberReader lineReader = new LineNumberReader(reader);){
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				matcher.reset(line); // reset the input
				if (matcher.find()) {
					l_match.put(lineReader.getLineNumber(), line.trim());
				}
			}
		} catch (IOException ex) {
			Logger.addStackTrace(ex);
		}
		return l_match;
	}

  final static Charset ENCODING = StandardCharsets.UTF_8;
  

  public static void main(String[] arguments) {
    LineMatcher lineMatcher = new LineMatcher();
    lineMatcher.find("c:/Development/WS/KC/de.usu.infoboard/uimodel/component/component_SelectDocument.sc.xml", " title=\"(?![%\\\"]).*");
  }
} 
