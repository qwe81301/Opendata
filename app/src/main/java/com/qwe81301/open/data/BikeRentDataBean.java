package com.qwe81301.open.data;

/**
 * author:       bearshih
 * project:      OpenData
 * date:         2019/9/20
 * version:
 * description:　腳踏車租借站資料
 * 使用即時資料，提供公共自行車租借狀態資訊，透過站點名稱、經緯度、地址、可借及可還等即時資訊，
 * 透過便利完整資訊，提升租借便利性來達成節能減碳之目的。
 */
public class BikeRentDataBean {

    //(目前測試)test 分支 單commit 合併(2-1)

    /**
     * stop_id : 0328
     * name : 一江公園
     * E_name : Yijiang Park
     * lon : 121.53146362304688
     * lat : 25.053159713745117
     * Address : 一江街 / 松江路132巷口(西北側)(鄰近四平陽光商圈)
     * E_Address : Yijiang St. /  Ln. 132, Songjiang Rd. intersection
     * city_id : 02
     * area : 中山區
     * E_area : Zhongshan Dist.
     * SumSpace : 36
     * Vehicles : 9
     * ParkingSpaces : 27
     * IsService : 1
     * et_update : 2019-09-20T17:28:09.31
     */

    private String stop_id;
    private String name;
    private String E_name;
    private double lon;
    private double lat;
    private String Address;
    private String E_Address;
    private String city_id;
    private String area;
    private String E_area;
    private int SumSpace;
    private int Vehicles;
    private int ParkingSpaces;
    private int IsService;
    private String et_update;

    public String getStop_id() {
        return stop_id;
    }

    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getE_name() {
        return E_name;
    }

    public void setE_name(String E_name) {
        this.E_name = E_name;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getE_Address() {
        return E_Address;
    }

    public void setE_Address(String E_Address) {
        this.E_Address = E_Address;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getE_area() {
        return E_area;
    }

    public void setE_area(String E_area) {
        this.E_area = E_area;
    }

    public int getSumSpace() {
        return SumSpace;
    }

    public void setSumSpace(int SumSpace) {
        this.SumSpace = SumSpace;
    }

    public int getVehicles() {
        return Vehicles;
    }

    public void setVehicles(int Vehicles) {
        this.Vehicles = Vehicles;
    }

    public int getParkingSpaces() {
        return ParkingSpaces;
    }

    public void setParkingSpaces(int ParkingSpaces) {
        this.ParkingSpaces = ParkingSpaces;
    }

    public int getIsService() {
        return IsService;
    }

    public void setIsService(int IsService) {
        this.IsService = IsService;
    }

    public String getEt_update() {
        return et_update;
    }

    public void setEt_update(String et_update) {
        this.et_update = et_update;
    }
}
