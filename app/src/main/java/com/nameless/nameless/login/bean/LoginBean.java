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
        private String  message;

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