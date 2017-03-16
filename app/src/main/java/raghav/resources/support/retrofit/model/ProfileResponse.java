package raghav.resources.support.retrofit.model;


public class ProfileResponse {
    private String status;
    private String first_name;
    private String last_name;
    private String user_telephone;
    private String user_street_address;
    private String user_city;
    private String user_country;
    private String user_email;
    private String user_photo;
    private String user_country_lat;
    private String user_country_long;
    private int user_average_rating;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUser_telephone() {
        return user_telephone;
    }

    public void setUser_telephone(String user_telephone) {
        this.user_telephone = user_telephone;
    }

    public String getUser_street_address() {
        return user_street_address;
    }

    public void setUser_street_address(String user_street_address) {
        this.user_street_address = user_street_address;
    }

    public String getUser_city() {
        return user_city;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public String getUser_country() {
        return user_country;
    }

    public void setUser_country(String user_country) {
        this.user_country = user_country;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUser_country_lat() {
        return user_country_lat;
    }

    public void setUser_country_lat(String user_country_lat) {
        this.user_country_lat = user_country_lat;
    }

    public String getUser_country_long() {
        return user_country_long;
    }

    public void setUser_country_long(String user_country_long) {
        this.user_country_long = user_country_long;
    }

    public int getUser_average_rating() {
        return user_average_rating;
    }

    public void setUser_average_rating(int user_average_rating) {
        this.user_average_rating = user_average_rating;
    }
}
