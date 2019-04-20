package ua.it.fbd.rtsqld.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Random;

public class Utils {
    public static String htmlNewLine(String text){
        return "<html>"+ text.replace("\n", "<br>") + "</html>";
    }

    public static String htmlFormater(String text){
        if (!text.isEmpty())
            text = "<html><div style=\"padding: 5px;\">"+text+"</div></html>";

        return text;
    }

    public static int getRandom(int max){
        Random randomGenerator = new Random();
        int ran = randomGenerator.nextInt(max);
        return  ran;
    }

    public static String[] getParsedTable(int colums, int rows, ResultSetMetaData rsmd,  ResultSet rs){
        String res = "";
        try{
            StringBuilder html = new StringBuilder("<html>\n" +
                    "\n" +
                    "<style>\n" +
                    "table {\n" +
                    "height: 5px;font-size: 8px; }" +
                    "\n" +
                    "table, td, th {\n" +
                    "  border: 1px solid black;\n" +
                    "}" +
                    "</style>\n" +
                    "\n" +
                    "<table><tr>");
            for (int i = 1; i <= colums; i++){
                html.append("<th>"+rsmd.getColumnName(i)+"</th>");
            }

            ArrayList<Columna> arrayList = new ArrayList<Columna>();
            while (rs.next()) {
                int i = 1;
                rows++;
                ArrayList<String> filas = new ArrayList<>();
                while(i <= colums) {
                    filas.add(rs.getString(i++));
                }
                if(filas.size() > 0){
                    Columna c = new Columna(filas);
                    arrayList.add(c);
                }

            }

            if(arrayList.size() > 0){
                for (int i = 0; i < arrayList.size(); i++){
                    ArrayList<String> filas = arrayList.get(i).getFilas();

                    if(filas.size() > 0){
                        html.append("<tr>");
                        for (int j = 0; j < filas.size(); j++){
                            html.append("<td>"+filas.get(j)+"</td>");
                        }
                        html.append("</tr>");
                    }
                }

            }
            html.append("</tr></table></html>");
            res = html.toString();
        }catch (Exception ex){
        }

        String rrs[] = {res, "" + rows};

        return rrs;
    }

    public static boolean isDDL(String query){
        boolean is = false;
        query = query.toString();
        if(query.contains("create") ||
           query.contains("alter") ||
           query.contains("insert") ||
           query.contains("update") ||
           query.contains("delete")){
            is = true;
        }

        return is;
    }

    public static int getExamIndex(String title){
        int index = 0;
        if(title.contains("T02B")){
            index = 1;
        }else if(title.contains("T04")){
            index = 2;
        }else if(title.contains("T03")){
            index = 3;
        }else if(title.contains("T05")){
            index = 4;
        }else if(title.contains("T08")){
            index = 5;
        }else if(title.contains("T09")){
            index = 6;
        }else if(title.contains("T09B")){
            index = 7;
        }else if(title.contains("T11")){
            index = 8;
        }else if(title.contains("T12")){
            index = 9;
        }

        return index;
    }


    public static String formatSeconds(int timeInSeconds)
    {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }
}
