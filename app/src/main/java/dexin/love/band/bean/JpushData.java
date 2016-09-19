package dexin.love.band.bean;

/**
 * Created by Administrator on 2016/9/7.
 */
public class JpushData {
    private String address;
    private String currentHeart;
    private String locationdescrible;
    private String identity;

    public JpushData() {
    }

    public JpushData(String address, String currentHeart, String locationdescrible, String identity) {
        this.address = address;
        this.currentHeart = currentHeart;
        this.locationdescrible = locationdescrible;
        this.identity = identity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentHeart() {
        return currentHeart;
    }

    public void setCurrentHeart(String currentHeart) {
        this.currentHeart = currentHeart;
    }

    public String getLocationdescrible() {
        return locationdescrible;
    }

    public void setLocationdescrible(String locationdescrible) {
        this.locationdescrible = locationdescrible;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return "JpushData{" +
                "address='" + address + '\'' +
                ", currentHeart='" + currentHeart + '\'' +
                ", locationdescrible='" + locationdescrible + '\'' +
                ", identity='" + identity + '\'' +
                '}';
    }
}
