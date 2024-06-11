
package model;


public class TongHoaDon {
 private int tonghoadon;
    private int donhuy;

    public int getDonhuy() {
        return donhuy;
    }

    public void setDonhuy(int donhuy) {
        this.donhuy = donhuy;
    }

    public int getTonghoadon() {
        return tonghoadon;
    }

    public void setTonghoadon(int tonghoadon) {
        this.tonghoadon = tonghoadon;
    }

    public TongHoaDon(int tonghoadon, int donhuy) {
        this.tonghoadon = tonghoadon;
        this.donhuy = donhuy;
    }

    public TongHoaDon() {
    }

}
