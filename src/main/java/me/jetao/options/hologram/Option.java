package me.jetao.options.hologram;

public class Option {

    private String text;
    private Response response;

    public Option(String text, Response response) {
        this.text = text;
        this.response = response;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Response getResponse() {
        return response;
    }
}
