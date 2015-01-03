package com.example.recongapi;

public class RecongAPI {
    private static RecongAPI recongAPI ;
	static {
		try {
		System.loadLibrary("recong");
		} catch (Error e) {
			e.printStackTrace();
		}
	}
	
	private RecongAPI() {
		
	}
	
	public native String doRecongFile(String filepath, int x, int y, String defaultvalue);
	public native String doRecongData(byte[] data, int x, int y, String defaultvalue);
	public static native int init(String license);
	public native int release();
	

	
	public static RecongAPI getRecongAPI() {
        if(null == recongAPI ) { 
              synchronized(RecongAPI.class){ 
                     if(null == recongAPI) 
                         recongAPI = new RecongAPI();
                         if (init("text_key") != 0) {
                             throw new IllegalStateException("Recong lib init error!!!");
                         }
              } 
         } 
       return recongAPI;
	}

}
