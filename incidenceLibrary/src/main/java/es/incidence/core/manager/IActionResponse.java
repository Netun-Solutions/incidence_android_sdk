package es.incidence.core.manager;

public class IActionResponse
{
    public boolean status;
    public String message;

    public IActionResponse(boolean status) {
        this.status = status;
    }
    public IActionResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public boolean isSuccess() {
        return status;
    }
}
