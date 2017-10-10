package org.gal.tools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

	private Properties m_prop = new Properties();
	
	private static Config m_config = null;
	
	private final static String CONFIG_NAME = "config.properties";
	
	public String getPath(){
		return Utils.getAppStoragePath()+File.separator;
	}
	
	private Config(){
		load();
	}
	
	public static Config getInstance(){
		if(m_config==null){
			m_config = new Config();
		}
		return m_config;
	}
	
	public String getProperty(String key, String defaultValue){
		return m_config.m_prop.getProperty(key, defaultValue);
	}
	
	public void setProperty(String key, String value){
		m_config.m_prop.setProperty(key, value);
		save();
	}
	
	
	public void load(){
		m_prop.clear();
		
		FileInputStream input = null;
		try{
			File file = new File(getPath()+CONFIG_NAME);
			if(!file.exists()){
				file.createNewFile();
			}
			input = new FileInputStream(file); 
			m_prop.load(input);
			
		}catch(IOException e){
			Logger.addStackTrace(e);
		}finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					Logger.addStackTrace(e);
				}
			}
		}
	}
	
	public void save(){
		FileOutputStream input = null;
		try{
			File file = new File(getPath()+CONFIG_NAME);
			if(!file.exists()){
				file.createNewFile();
			}
			input = new FileOutputStream(file); 
			m_prop.store(input,null);
			
		}catch(IOException e){
			Logger.addStackTrace(e);
		}finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					Logger.addStackTrace(e);
				}
			}
		}
	}

}
