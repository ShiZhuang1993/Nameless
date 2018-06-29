package com.nameless.nameless.login.bean;

/**
 * ===================================
 * describe:
 * date:2018/6/28
 * author:zhuang
 * ===================================
 */

public class EvaluteBean {

    /**
     * status : {"code":"SUCCESS","message":null}
     */

    private StatusBean status;

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
