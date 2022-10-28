/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package appagenda;

import entidades.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author usu2dam
 */
public class AgendaViewController implements Initializable {

    private EntityManager entityManager;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona, String> columnNombre;
    @FXML
    private TableColumn<Persona, String> columnApellidos;
    @FXML
    private TableColumn<Persona, String> columnEmail;
    @FXML
    private TableColumn<Persona, String> columnProvincia;
    @FXML
    private Label nombre;
    @FXML
    private Label apellidos;
    @FXML
    private TextField introNombre;
    @FXML
    private TextField introApellidos;
    @FXML
    private Button guardar;
    
    private Persona personaSeleccionada;

    public void setEntityManager(EntityManager entityManager){
        this.entityManager=entityManager;
    }
    
    public void cargarTodasPersonas(){
        Query queryPersonaFindAll=
        entityManager.createNamedQuery("Persona.findAll");
        List<Persona> listPersona = queryPersonaFindAll.getResultList();
        tableViewAgenda.setItems(FXCollections.observableArrayList(listPersona)
        );
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnProvincia.setCellValueFactory(cellData->{
            SimpleStringProperty property=new SimpleStringProperty();
            if (cellData.getValue().getProvincia()!=null){
                property.setValue(cellData.getValue().getProvincia().getNombre());
            }
            return property;
        });
        
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener(
            (observable,oldValue,newValue)->{
            personaSeleccionada=newValue;
            if (personaSeleccionada != null){
                introNombre.setText(personaSeleccionada.getNombre());
                introApellidos.setText(personaSeleccionada.getApellidos());
            } else {
                introNombre.setText("");
                introApellidos.setText("");
            }
        });
    }    

    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        if (personaSeleccionada != null){
            personaSeleccionada.setNombre(introNombre.getText());
            personaSeleccionada.setApellidos(introApellidos.getText());
            entityManager.getTransaction().begin();
            entityManager.merge(personaSeleccionada);
            entityManager.getTransaction().commit();
            
            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            
            TablePosition pos = new
            TablePosition(tableViewAgenda,numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }
    
}
