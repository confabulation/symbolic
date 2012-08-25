package confabulation;

public class ConfabulationException extends RuntimeException{

	private static final long serialVersionUID = 888498271523250030L;
	
	String m;
	
	public ConfabulationException(String message){
		this.m=message;
	}

	public String toString(){
		return m;
	}
}
