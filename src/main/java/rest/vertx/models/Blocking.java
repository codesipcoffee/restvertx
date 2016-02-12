package rest.vertx.models;

public class Blocking {

	private boolean blocking;
	
	private boolean serial;
	
	public Blocking()
	{
		setBlocking(false);
	}
	
	public Blocking(boolean blocking, boolean serial)
	{
		setBlocking(blocking);
		
		setSerial(serial);
	}

	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	public boolean isSerial() {
		return serial;
	}

	public void setSerial(boolean serial) {
		this.serial = serial;
	}
}
