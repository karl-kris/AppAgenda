/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package appagenda;

import entidades.Persona;
import entidades.Provincia;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * FXML Controller class
 *
 * @author kristian
 */
public class PersonaDetalleViewController implements Initializable {

    @FXML
    private TextField rowNombre;
    @FXML
    private TextField rowApellidos;
    @FXML
    private TextField rowTelefono;
    @FXML
    private TextField rowEmail;
    @FXML
    private TextField rowHijos;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;
    @FXML
    private ComboBox<Provincia> rowProvincia;
    @FXML
    private CheckBox rowJubilacion;
    @FXML
    private RadioButton soltero;
    @FXML
    private RadioButton casado;
    @FXML
    private RadioButton viudo;
    @FXML
    private DatePicker rowFecha;
    @FXML
    private TextField rowSalario;
    @FXML
    private ImageView image;
    @FXML
    private Button btnExaminar;
    
    private Pane rootAgendaView;
    @FXML
    private AnchorPane rootPersonaDetalleView;
    
    private TableView tableViewPrevio;
    private Persona persona;
    private EntityManager entityManager;
    private boolean nuevaPersona;
    
    public static final char CASADO='C';
    public static final char SOLTERO='S';
    public static final char VIUDO='V';

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        entityManager.getTransaction().rollback();
        int numFilaSeleccionada =

        tableViewPrevio.getSelectionModel().getSelectedIndex();

        TablePosition pos = new TablePosition(tableViewPrevio,numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
        
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
    }

    @FXML
    private void onActionButtonGuadar(ActionEvent event) {
        persona.setNombre(rowNombre.getText());
        persona.setApellidos(rowApellidos.getText());
        persona.setTelefono(rowTelefono.getText());
        persona.setEmail(rowEmail.getText());
        if (nuevaPersona){
        entityManager.persist(persona);
        } else {
        entityManager.merge(persona);
        }
        entityManager.getTransaction().commit();
        
        int numFilaSeleccionada;
        if (nuevaPersona){
        tableViewPrevio.getItems().add(persona);
        numFilaSeleccionada = tableViewPrevio.getItems().size()- 1;
        tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
        tableViewPrevio.scrollTo(numFilaSeleccionada);
        } else {
        numFilaSeleccionada=

        tableViewPrevio.getSelectionModel().getSelectedIndex();
        tableViewPrevio.getItems().set(numFilaSeleccionada,persona);
        }
        TablePosition pos = new TablePosition(tableViewPrevio,

        numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
        
        StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
    }

    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
    }
    
    public void setRootAgendaView(Pane rootAgendaView){
        this.rootAgendaView = rootAgendaView;
    }
    
    public void setTableViewPrevio(TableView tableViewPrevio){
        this.tableViewPrevio=tableViewPrevio;
    }
    public void setPersona(EntityManager entityManager, Persona persona, Boolean nuevaPersona){
        this.entityManager = entityManager;
        entityManager.getTransaction().begin();
        if (!nuevaPersona){
            this.persona=entityManager.find(Persona.class,persona.getId());
        } else {
            this.persona=persona;
        }
            this.nuevaPersona=nuevaPersona;
    }
    
    public void mostrarDatos(){
        rowNombre.setText(persona.getNombre());
        rowApellidos.setText(persona.getApellidos());
        rowTelefono.setText(persona.getTelefono());
        rowEmail.setText(persona.getEmail());
        if (persona.getNumHijos() != null){
            rowHijos.setText(persona.getNumHijos().toString());
        }
        if (persona.getSalario() != null){
            rowSalario.setText(persona.getSalario().toString());
        }
        if (persona.getJubilado() != null){
            rowJubilacion.setSelected(persona.getJubilado());
        }
        if (persona.getEstadoCivil() != null){
            switch(persona.getEstadoCivil()){
            case CASADO:
            casado.setSelected(true);
            break;
            case SOLTERO:
            soltero.setSelected(true);
            break;
            case VIUDO:
            viudo.setSelected(true);
            break;
            }
        }
        if (persona.getFechaNacimiento() != null){
            Date date=persona.getFechaNacimiento();
            Instant instant=date.toInstant();
            ZonedDateTime zdt=instant.atZone(ZoneId.systemDefault());
            LocalDate localDate=zdt.toLocalDate();
            rowFecha.setValue(localDate);
        }
    }
    
}
