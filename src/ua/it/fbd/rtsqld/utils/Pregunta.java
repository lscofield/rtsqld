package ua.it.fbd.rtsqld.utils;

import java.util.ArrayList;

public class Pregunta {
    private String pregunta;
    private Boolean superada;
    private String num;
    private ArrayList<String> notas;
    private ArrayList<String> queries;

    public Pregunta(String pregunta, Boolean superada, String num,
                    ArrayList<String> notas, ArrayList<String> queries){
        this.pregunta = pregunta;
        this.superada = superada;
        this.num = num;
        this.notas = notas;
        this.queries = queries;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public Boolean getSuperada() {
        return superada;
    }

    public void setSuperada(Boolean superada) {
        this.superada = superada;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public ArrayList<String> getNotas() {
        return notas;
    }

    public void setNotas(ArrayList<String> notas) {
        this.notas = notas;
    }

    public ArrayList<String> getQueries() {
        return queries;
    }

    public void setQueries(ArrayList<String> queries) {
        this.queries = queries;
    }
}
