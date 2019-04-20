package ua.it.fbd.rtsqld;

import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.concurrent.Worker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.swing.table.DefaultTableCellHeaderRenderer;
import ua.it.fbd.rtsqld.utils.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.time.Period;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class Inicio {

    private JPanel panelRoot;
    private JTextPane mCode;
    private JLabel mPregunta;
    private JLabel mConsole;
    private JButton btnCorregir;
    private JLabel htmlTableRemote;
    private JButton btnPort;
    private JButton btnClear;
    private JLabel mLabConsola;
    private JScrollPane mScrollConsole;
    private JButton btnConect;
    private JButton btnSkip;
    private JLabel mFeedback;
    private JLabel tTime;
    private JLabel tEjercicio;

    private String queries = "";
    private String database = "tiendaonline";
    private String username = "";
    private String pass = "";
    private String host = "";
    private String console = "";
    private ArrayList<String> examenes = new ArrayList<>();

    private String currSssion = "";
    ArrayList<Pregunta> classPreguntas = new ArrayList<>();
    ArrayList<Pregunta> classPreguntasBackup = new ArrayList<>();
    private int random_int = 0, startExam = 0;

    private Connection conn;
    private Boolean loaded = false;
    private Pregunta currentQuestion;

    private Timer t;

    private void prepareQuestions(){
        if(loaded){
            random_int = Utils.getRandom(classPreguntas.size());
            currentQuestion = classPreguntas.get(random_int);
            mPregunta.setFont(new Font("Serif", Font.PLAIN, 13));
            mPregunta.setText(Utils.htmlFormater(currentQuestion.getPregunta()));
            tEjercicio.setText("Ejercicio: " + currentQuestion.getNum() + " " + currSssion);
            classPreguntas.remove(random_int);
            tTime.setForeground(Color.BLACK);
            countDown();
        }
    }

    private void prepareConfig(){
        // config
        JSONParser parser = new JSONParser();
        Object obj;

        try{
            obj = parser.parse(new FileReader("config.json"));
            JSONObject config = (JSONObject) obj;
            host = config.get("servidor").toString();
            database = config.get("db").toString();
            username = config.get("usuario").toString();
            pass = config.get("password").toString();
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,
                    "Error al cargar el fichero de configuración: Asegúrate de que esté en la misma carpeta que el ejecutable .jar", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void settingExam(){
        prepareData(true);
        Object[] exams = examenes.toArray();
        String s = (String)JOptionPane.showInputDialog(
                null,
                "Selecciona una sesión para practicar",
                "Sesión",
                JOptionPane.PLAIN_MESSAGE,
                null,
                exams,
                examenes.get(0));

        if ((s != null) && (s.length() > 0)) {
            tEjercicio.setText(s);
            startExam = Utils.getExamIndex(s);
        }else{
            System.exit(0);
        }
    }

    public Inicio() {
        try {
            settingExam();
            mConsole.setBorder(new EmptyBorder(0,1,0,0));
            mConsole.setFont(new Font("Serif", Font.PLAIN, 12));
            mLabConsola.setFont(new Font("Serif", Font.BOLD, 13));
            mLabConsola.setForeground(Color.RED);
            mLabConsola.setText("No conectado");
            btnSkip.setToolTipText("Siguiente pregunta (Aleatoria)");
            htmlTableRemote.setToolTipText("Resultado de tu query");
            tTime.setBorder(new EmptyBorder(0,10,0,0));
            tEjercicio.setBorder(new EmptyBorder(0,30,0,0));
            prepareConfig();
        } catch (Exception e) {

        }

        btnCorregir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queries = mCode.getText();
                if(!queries.isEmpty()){
                   execTable(queries);
                }else{
                    console += ("\n ERROR: Debes escribir alguna query.");
                    setText(mConsole);
                }
            }
        });

        btnPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    console = "\n Tarea: Abriendo puerto...";
                    setText(mConsole);
                    URL url = new URL(Consts.OPEN_PORT_URI);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.setRequestProperty("User-Agent",Consts.USER_AGENT);
                    int responseCode = c.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                c.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        String html = response.toString();
                        if(html.contains("Pedido")){
                            html = html.split("<pre>")[1];
                            html = html.split("</pre>")[0];
                            html = html.replaceAll("-", "");
                        }else{
                            html = "En menos de 1 minuto debes de poder acceder";
                        }
                        console = "\n Puerto abierto: " + html;
                        setText(mConsole);
                    } else {
                        console = "\n ERROR: No se pudo abrir el puerto, prueba otra vez.";
                        setText(mConsole);
                        btnPort.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try{
                                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                        Desktop.getDesktop().browse(new URI(Consts.OPEN_PORT_URI));
                                    }
                                }catch (Exception es){

                                }
                            }
                        });
                    }
                }catch (Exception ex){
                    console = "\n ERROR: No se pudo abrir el puerto, prueba otra vez.";
                    setText(mConsole);
                    btnPort.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try{
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                    Desktop.getDesktop().browse(new URI(Consts.OPEN_PORT_URI));
                                }
                            }catch (Exception es){

                            }
                        }
                    });
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                console = "No conectado!";
                if(conn != null)
                    console = "Conectado: " + username + "@" + host + "/" + database;
                setText(mConsole);
            }
        });
        btnConect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (conn != null){
                    if (btnConect.getText().equals("Desconectar")){
                        close();
                        btnConect.setText("Conectar");
                        mLabConsola.setForeground(Color.RED);
                        mLabConsola.setText("No conectado");
                        console = "No conectado!";
                        setText(mConsole);
                        conn = null;
                        if (t != null)
                            t.cancel();
                    }else{
                        connect();
                    }
                }else{
                    connect();
                }
            }
        });
        btnSkip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(classPreguntas.size() == 0){
                    classPreguntas = new ArrayList<>();
                    classPreguntas.addAll(classPreguntasBackup);
                }
                mCode.setText("");
                mFeedback.setText("");
                btnCorregir.setEnabled(true);
                if (t != null)
                    t.cancel();
                prepareQuestions();
            }
        });
    }

    private String execTable(String query){
        String result = "";
        try {
            if (conn != null){
                console += ("\n Tarea: Ejecutando...");
                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery(query);
                try{
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int colums = rsmd.getColumnCount();
                    int rows = 0;
                    String html = "";
                    if(colums > 0){
                        String [] html_data = Utils.getParsedTable(colums,rows,rsmd,rs);
                        html = html_data[0];
                        htmlTableRemote.setText(html);
                        console += ("\n Resultado: " + html_data[1] + " registros");
                        setText(mConsole);
                    }else{
                        console += ("\n Resultado: Sin resultados");
                        setText(mConsole);
                    }
                    rs.close();
                    try{
                        rs = stmt.executeQuery(currentQuestion.getQueries().get(0));
                        String [] html_data= Utils.getParsedTable(colums,0,rsmd,rs);
                        String html_prof =  html_data[0];
                        if(html.equals(html_prof)){
                            //respuesta correcta
                            mFeedback.setFont(new Font("Serif", Font.BOLD, 16));
                            mFeedback.setForeground(Color.GREEN);
                            mFeedback.setText("CORRECTO");
                            console += ("\n Resultado: Respuesta correcta");
                            setText(mConsole);
                        }else{
                            mFeedback.setFont(new Font("Serif", Font.BOLD, 16));
                            mFeedback.setForeground(Color.RED);
                            mFeedback.setText("INCORRECTO");
                            console += ("\n Resultado: Respuesta incorrecta");
                            setText(mConsole);
                        }

                        getCodeResult();
                        if (t != null)
                            t.cancel();
                    }catch (Exception exx){
                        console += ("\n ERROR: " + exx.getMessage());
                        setText(mConsole);
                    }
                }catch (Exception ee){
                    console += ("\n ERROR: " + ee.getMessage());
                    setText(mConsole);
                }
            }else{
                console += ("\n ERROR: No estás conectado");
                setText(mConsole);
            }
        } catch (SQLException e1) {
            console += ("\n ERROR: " + e1.getMessage());
            setText(mConsole);
        }

        return result;
    }

    private void getCodeResult(){
        String queries = "TU SOLUCIÓN: \n" + mCode.getText() + "\n\nPOSIBLES SOLUCIONES: ";
        for (int q = 0; q < currentQuestion.getQueries().size(); q++){
            queries += "\n\nSolución " + (q+1) + ": " + currentQuestion.getQueries().get(q);
        }
        String notas = "\n\nNOTAS IMPORTANTES: ";
        for (int q = 0; q < currentQuestion.getNotas().size(); q++){
            notas += "\n\n" + currentQuestion.getNotas().get(q);
        }
        if(currentQuestion.getNotas().size() == 0)
            notas += "Ninguna";
        mCode.setText(queries + notas);
        btnCorregir.setEnabled(false);
    }

    private void prepareData(boolean titsOnly) {
        JSONParser parser = new JSONParser();

        try {

            /**/
            Object obj;
            try{
                obj = parser.parse(new FileReader("rtsqld.json"));
            }catch (Exception ex){
                InputStream is =  getClass().getResourceAsStream("rtsqld.json");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                obj = parser.parse(br);
            }

            JSONArray jsonArray = (JSONArray) obj;
            if(titsOnly){
                for (int i = 0; i < jsonArray.size(); i++){
                    JSONObject examen = (JSONObject) jsonArray.get(i);
                    examenes.add(examen.get("titulo").toString());
                }
            }else{
                JSONObject examen = (JSONObject) jsonArray.get(startExam);
                JSONArray preguntas = (JSONArray) examen.get("preguntas");
                currSssion = examen.get("titulo").toString();

                for (int i = 0; i < preguntas.size(); i++){
                    JSONObject prs = (JSONObject) preguntas.get(i);
                    String pregunta = prs.get("pregunta").toString();
                    String num  =  prs.get("num").toString();
                    Boolean superada =  Boolean.valueOf(prs.get("superada").toString());
                    pregunta = Utils.htmlFormater(pregunta);


                    JSONArray queries = (JSONArray) prs.get("queries");
                    ArrayList<String> classQueries = new ArrayList<>();

                    for (int j = 0; j < queries.size(); j++){
                        classQueries.add(queries.get(j).toString());
                    }

                    JSONArray notas = (JSONArray) prs.get("notas");
                    ArrayList<String> classNotas = new ArrayList<>();

                    for (int j = 0; j < notas.size(); j++){
                        classNotas.add(notas.get(j).toString());
                    }

                    Pregunta p = new Pregunta(pregunta, superada, num, classNotas, classQueries);
                    classPreguntas.add(p);
                }

                loaded = true;
                classPreguntasBackup.addAll(classPreguntas);
            }
        } catch (Exception e) {
        }
    }

    private void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setText(final JLabel label){
        label.setText(Utils.htmlNewLine(console));
        label.paintImmediately(label.getVisibleRect());
        mScrollConsole.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
    }


    private void connect(){
        try {
            console = "Conectando...";
            setText(mConsole);
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(username);
            dataSource.setPassword(pass);
            dataSource.setServerName(host);
            dataSource.setLoginTimeout(5);
            conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("use "+database+";");
            stmt.close();
            console = "Conectado: " + username + "@" + host + "/" + database;
            mLabConsola.setForeground(Color.BLACK);
            mLabConsola.setText(console);
            setText(mConsole);
            btnConect.setText("Desconectar");

            if(conn != null){
                btnPort.setEnabled(false);
                prepareData(false);
                prepareQuestions();
            }
        } catch (Exception e) {
            mLabConsola.setForeground(Color.RED);
            mLabConsola.setText("No conectado");
            console = "No conectado: Prueba abrir el puerto.";
            setText(mConsole);
            btnConect.setText("Conectar");
        }
    }


    public static void main(String [] args){
        JFrame frame = new JFrame(Consts.APP_NAME);
        frame.getContentPane().add(new Inicio().panelRoot);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().requestFocusInWindow();
        try {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Inicio.class.getResource("logo.png")));
        } catch (Exception e) {
        }

        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void countDown() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            int seconds = 600;
            @Override
            public void run() {
                String tiempo = Utils.formatSeconds(seconds--);
                tTime.setText("Tiempo restante: " + tiempo);
                if(seconds < 59 && seconds > 1){
                    tTime.setForeground(Color.RED);
                    tTime.setText("Tiempo restante: " + tiempo);
                }
                if(seconds < 1) {
                    t.cancel();
                    mFeedback.setFont(new Font("Serif", Font.BOLD, 16));
                    mFeedback.setForeground(Color.RED);
                    mFeedback.setText("TIEMPO AGOTADO");
                    tTime.setForeground(Color.RED);
                    tTime.setText("Tiempo restante: 00:00:00");
                    console += ("\n Resultado: Se ha agotado el tiempo");
                    setText(mConsole);
                }
            }
        }, 0, 1000);
    }

}
