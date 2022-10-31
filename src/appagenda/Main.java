/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */
package appagenda;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author usu2dam
 */
public class Main extends Application {
    
    // Entity manager y StackPane como root de las vistas
    private EntityManagerFactory emf;
    private EntityManager em;
    StackPane rootMain = new StackPane();
        
        
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        // Cargamos doumento FXML AgendaView como principal (luego lo cambiaremos mediante StackPane rootMain)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AgendaView.fxml"));
        Pane rootAgendaView = fxmlLoader.load();
        rootMain.getChildren().add(rootAgendaView);
        emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        em = emf.createEntityManager();
        
        // Cargamos el controller de AgendaView para poder asignarle a la tabla los datos que ya están en la base de datos
        AgendaViewController agendaViewController = (AgendaViewController)fxmlLoader.getController();
        agendaViewController.setEntityManager(em);
        agendaViewController.cargarTodasPersonas();

        // La escena principal es el StackPane que contendrá las dos vistas Agena y Persona detalle
        Scene scene = new Scene(rootMain);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show(); 
    }

    @Override
    public void stop() throws Exception {
        // Al acabar la conexión cerramos tanto el entity manager como el EM factory
        em.close();
        emf.close();
        try{
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
        } catch(SQLException ex) {
        }
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
