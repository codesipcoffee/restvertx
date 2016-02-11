package rest.vertx.models;

import java.util.Map;

/**
 * This class permits the user to give more information about the answer he wants to send to the client
 * For example, he could change the status code, or add additional headers ....
 * In this first version, only the status code will be implemented, but adding new custom fields shouldn't
 * be too difficult
 *
 * Giuliano FRANCHETTO
 */
public class RestResponse {

    /* The body of the response to send */
    private String body;

    /*The status code of the response to send*/
    private int statusCode;
    
    /* Map of headers */
    private Map<String, String> headers;

    public RestResponse(String body, int statusCode, Map<String, String> headers) {
        this.body = body;
        this.statusCode = statusCode;
        this.headers = headers;
    }
    
    public RestResponse(String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    /* Default constructor, assuming that the status code is 200 (no error) */
    public RestResponse(String body) {
        this.body = body;
        this.statusCode = 200;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
}
