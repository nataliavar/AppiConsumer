package cr.ac.ucr.com.apiconsumer.models;

public class Weather {
    /*
      "id": 800,
              "main": "Clear",
              "description": "clear sky",
              "icon": "01n"*/

    private int id;
    private String main;
    private String description;
    private String icon;

    public Weather(){
    }

    public Weather(int id, String main, String description, String icon){
        this.id=id;
        this.main=main;
        this.description=description;
        this.icon=icon;
    }

    public int getId(){
        return this.id;
    }
    public String getMain(){
        return this.main;
    }

    public String getDescription(){
        return  this.description;
    }

    public String getIcon(){
        return this.icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "id=" + id +
                ", main='" + main + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
