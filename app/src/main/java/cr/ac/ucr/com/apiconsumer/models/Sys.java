package cr.ac.ucr.com.apiconsumer.models;

public class Sys {

    private String country;

    public Sys(){}

    public Sys(String country){
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Sys{" +
                "country='" + country + '\'' +
                '}';
    }
}
