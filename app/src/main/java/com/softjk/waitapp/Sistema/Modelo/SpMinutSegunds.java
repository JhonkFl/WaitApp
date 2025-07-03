package com.softjk.waitapp.Sistema.Modelo;

public class SpMinutSegunds {

    private static int Tiempo;

    public SpMinutSegunds(){ }

    public SpMinutSegunds(int tiempo) {this.Tiempo = tiempo;}

    public static int getTiempo() {
        return Tiempo;
    }

    public static void setTiempo(String valor) {
        if (valor.equals("5 minutos")){
            Tiempo = 5;
        }else if (valor.equals("10 minutos")){
            Tiempo = 10;
        }else if (valor.equals("15 minutos")){
            Tiempo = 15;
        }else if (valor.equals("20 minutos")){
            Tiempo = 20;
        }else if (valor.equals("25 minutos")){
            Tiempo = 25;
        }else if (valor.equals("30 minutos")){
            Tiempo = 30;
        }else if (valor.equals("45 minutos")){
            Tiempo = 45;
        }else if (valor.equals("50 minutos")){
            Tiempo = 50;
        }else if (valor.equals("1 hora")){
            Tiempo = 60;
        }else if (valor.equals("1:15 Horas")){
            Tiempo = 75;
        }else if (valor.equals("1:30 Horas")){
            Tiempo = 90;
        }else if (valor.equals("1:45 Horas")){
            Tiempo = 105;
        }else if (valor.equals("2 Horas")){
            Tiempo = 120;
        }
    }
}


