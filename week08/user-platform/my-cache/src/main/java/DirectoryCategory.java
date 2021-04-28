public enum DirectoryCategory {
	
	AAA006("AAA006","阿道夫"),
	AAA007("AAA007","we"),
	AAA008("AAA008","撒旦法")
	;
	
	private String code;
	private String name;
	
	private DirectoryCategory(String code,String name){
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	
	
	
}