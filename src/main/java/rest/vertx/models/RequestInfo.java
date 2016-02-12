package rest.vertx.models;

public class RequestInfo {

	private Blocking blocking;
	
	public RequestInfo() {
		
	}
	
	public RequestInfo(Blocking blocking) {
		this.blocking = blocking;
	}

	public Blocking getBlocking() {
		return blocking;
	}

	public void setBlocking(Blocking blocking) {
		this.blocking = blocking;
	}	
}
