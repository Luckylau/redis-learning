package org.spring.springboot.model;

/**
 * @author luckylau
 * @date 2017/12/13/013 9:57
 *
 * 业务对应的model
 */
public class City {
    /**
     * 省份
     */
    private String province;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 描述
     */
    private String description;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
