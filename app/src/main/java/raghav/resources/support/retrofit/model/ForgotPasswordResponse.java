package raghav.resources.support.retrofit.model;

public class ForgotPasswordResponse {


    /**
     * status : true
     * msg : An Email has been sent for password reset.
     */

    private boolean status;
    private String msg;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
