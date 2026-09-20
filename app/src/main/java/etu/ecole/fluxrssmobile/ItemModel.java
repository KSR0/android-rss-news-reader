package etu.ecole.fluxrssmobile;

public class ItemModel {
    private String title;
    private String description;
    private String date;

    public ItemModel(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Title : " + this.title + "\n" + "Description : " + this.description + "\n" + "Date : " + this.date + "\n";
    }
}
