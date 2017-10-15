package org.gal.tools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Available configuration properties
 * 
 * #username to database 
 * user=cvstools
 * 
 * #password to database
 * password=cvstools
 * 
 * #location of database
 * database=C:/Development/eclipse/plugins/cvs.tools/cvstools.db
 * 
 * #jira username 
 * JIRA_username=
 * 
 * #jira password 
 * JIRA_password=
 * 
 * #JIRA API REST url to insert a new comment 
 * # the placeholder ${issue} will be replaced with issue number from a commit's comment
 * JIRA_addCommentRestUrl=http://JIRA_URL/rest/api/2/issue/${issue}/comment
 * 
 * #template of a JIRA comment 
 * # It's able to be used these placeolders: 
 * # ${issue} - issue number # ${version} - name of the current workspace #
 * ${resources} - list of resources in a change set 
 * JIRA_commentTemplate=CHANGES in ${version}:\n${resources}
 * 
 * # direct link to a JIRA issue 
 * JIRA_issueUrl=http://JIRA_URL/browse/${issue}
 * 
 * #pattern to resolve a JIRA issue number from a commit's comment
 * JIRA_issueNumberPattern = ^[A-Z]+-\d+
 * 
 * #if it's set, to the list of resources in JIRA comment is added before a filename type of operation 
 * # I -inserted, U-updated, D-deleted
 * addOperationTypeToResource=true
 * 
 * #limit of returned change sets
 * changeSetsLimit=100000
 */
public class Config {

	private Properties m_prop = new Properties();

	private static Config m_config = null;

	private final static String CONFIG_NAME = "config.properties";

	public String getPath() {
		return Utils.getAppStoragePath() + File.separator;
	}

	private Config() {
		load();
	}

	public static Config getInstance() {
		if (m_config == null) {
			m_config = new Config();
		}
		return m_config;
	}

	public String getProperty(String key, String defaultValue) {
		return m_config.m_prop.getProperty(key, defaultValue);
	}

	public void setProperty(String key, String value) {
		m_config.m_prop.setProperty(key, value);
		save();
	}

	public void load() {
		m_prop.clear();

		FileInputStream input = null;
		try {
			File file = new File(getPath() + CONFIG_NAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			input = new FileInputStream(file);
			m_prop.load(input);
			Logger.addMessage("Config loaded:"+getPath() + CONFIG_NAME);
		} catch (IOException e) {
			Logger.addStackTrace(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Logger.addStackTrace(e);
				}
			}
		}
	}

	public void save() {
		FileOutputStream input = null;
		try {
			File file = new File(getPath() + CONFIG_NAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			input = new FileOutputStream(file);
			m_prop.store(input, null);

		} catch (IOException e) {
			Logger.addStackTrace(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Logger.addStackTrace(e);
				}
			}
		}
	}

}
