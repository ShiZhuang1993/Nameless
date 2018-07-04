package com.nameless.nameless.login.bean;

/**
 * ===================================
 * describe:
 * date:2018/6/28
 * author:zhuang
 * ===================================
 */

public class LoginBean {

    /**
     * result : http://192.168.10.100:3000/user/275
     * status : {"code":"SUCCESS","message":null}
     */

    private String result;
    private StatusBean status;
    private String share_url;

    public String getShare_url() {
        return share_url == null ? "" : share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public static class StatusBean {
        /**
         * code : SUCCESS
         * message : null
         */

        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}