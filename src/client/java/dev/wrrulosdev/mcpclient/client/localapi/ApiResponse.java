package dev.wrrulosdev.mcpclient.client.localapi;

public class ApiResponse {
    private String status;
    private String title;
    private Object data; 

    /**
     * Creates an API response with a given status and title.
     *
     * @param status the response status, e.g., "success" or "error"
     * @param title a short descriptive title or message for the response
     */
    public ApiResponse(String status, String title) {
        this.status = status;
        this.title = title;
    }

    /**
     * Creates an API response with a given status, title, and associated data.
     *
     * @param status the response status, e.g., "success" or "error"
     * @param title a short descriptive title or message for the response
     * @param data optional additional data to include in the response
     */
    public ApiResponse(String status, String title, Object data) {
        this.status = status;
        this.title = title;
        this.data = data;
    }

    /**
     * Creates an error response with the given title.
     *
     * @param title the title or message describing the error
     * @return an {@code ApiResponse} instance representing an error
     */
    public static ApiResponse error(String title) {
        return new ApiResponse("error", title);
    }

    /**
     * Creates a success response with the given title.
     *
     * @param title the title or message describing the success
     * @return an {@code ApiResponse} instance representing a success
     */
    public static ApiResponse success(String title) {
        return new ApiResponse("success", title);
    }

    /**
     * Creates a success response with the given title and additional data.
     *
     * @param title the title or message describing the success
     * @param data the data to include in the success response
     * @return an {@code ApiResponse} instance representing a success with data
     */
    public static ApiResponse success(String title, Object data) {
        return new ApiResponse("success", title, data);
    }

    /**
     * Retrieves the status of this API response.
     *
     * @return the status of the response
     */
    public String getStatus() { 
        return status; 
    }

    /**
     * Retrieves the title or message of this API response.
     *
     * @return the title or message
     */
    public String getTitle() { 
        return title; 
    }

    /**
     * Retrieves any data attached to this API response.
     *
     * @return the data associated with the response, or {@code null} if none
     */
    public Object getData() { 
        return data; 
    }
}
