package com.iot.core.log.model;

//Api操作日志
public class LogApi extends LogAbstract{

    private static final long serialVersionUID = 1L;

    /**
     * 日志类型。
     */
    private String type;

    /**
     * 操作标题，对应@ApiLog中的value。
     */
    private String title;

    /**
     * 方法执行耗时，单位为毫秒。
     */
    private String time;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
