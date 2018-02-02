package org.spring.springboot.domain;


import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

/**
 * @author luckylau
 * @date 2017/12/13/013 9:57
 *
 * 数据库对应的model
 */
public class CityEntity {

    private int id;
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

    private Date createTime;

    private Date modifyTime;

    public transient final static int VERSION = 1;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String generateCityCacheKey(String city){
        //处理中文
        String md5City = DigestUtils.md5Hex(city);
        return String.format("version_%s_city_%s", VERSION, md5City);
    }

}
