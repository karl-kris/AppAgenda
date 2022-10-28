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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author usu2dam
 */
public class Main extends Application {
    
    private EntityManagerFactory emf;
    private EntityManager em;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AgendaView.fxml"));
        Parent root = fxmlLoader.load();
        emf=Persistence.createEntityManagerFactory("AppAgendaPU");
        em=emf.createEntityManager();
        
        AgendaViewController agendaViewController = (AgendaViewController)fxmlLoader.getController();
        agendaViewController.setEntityManager(em);


        
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("App Agenda");
        primaryStage.setScene(scene);
        primaryStage.show(); 
    }

    @Override
    public void stop() throws Exception {
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
