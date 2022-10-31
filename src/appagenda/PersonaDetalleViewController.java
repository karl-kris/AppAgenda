/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package appagenda;

import entidades.Persona;
import entidades.Provincia;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * FXML Controller class
 *
 * @author Kristian Johansson Dougal
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
    private ImageView imageViewFoto;
    
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
    
    public static final String CARPETA_FOTOS="src/appagenda/Fotos";


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    // Cancela en caso de ser la opción elegida
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

    // Guarda los datos comprobando antes si son correctos
    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        
        boolean errorFormato = false;
        
        persona.setNombre(rowNombre.getText());
        persona.setApellidos(rowApellidos.getText());
        persona.setTelefono(rowTelefono.getText());
        persona.setEmail(rowEmail.getText());
        if (!rowHijos.getText().isEmpty()){
            try {
                persona.setNumHijos(Short.valueOf(rowHijos.getText()));
            } catch(NumberFormatException ex){
                errorFormato = true;
                Alert alert = new Alert(AlertType.INFORMATION, "Número de hijos no válido");
                alert.showAndWait();
                rowHijos.requestFocus();
            }
        }
        
        if (!rowSalario.getText().isEmpty()){
            try {
                persona.setSalario(BigDecimal.valueOf(Double.valueOf(rowSalario.getText()).doubleValue()));
            } catch(NumberFormatException ex) {
                errorFormato = true;
                Alert alert = new Alert(AlertType.INFORMATION, "Salario no válido");
                alert.showAndWait();
                rowSalario.requestFocus();
            } 
        }

        persona.setJubilado(rowJubilacion.isSelected());
        
        if (casado.isSelected()){
            persona.setEstadoCivil(CASADO);
        } else if (soltero.isSelected()){
            persona.setEstadoCivil(SOLTERO);
        } else if (viudo.isSelected()){
            persona.setEstadoCivil(VIUDO);
        }
        
        if (rowFecha.getValue() != null){
            LocalDate localDate = rowFecha.getValue();
            ZonedDateTime zonedDateTime =
            localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            persona.setFechaNacimiento(date);
        } else {
            persona.setFechaNacimiento(null);
        }
        
        if (rowProvincia.getValue() != null){
            persona.setProvincia(rowProvincia.getValue());
        } else {
            Alert alert = new Alert(AlertType.INFORMATION,"Debe indicar una provincia");
            alert.showAndWait();
            errorFormato = true;
        }


        
        if (!errorFormato) { // Los datos introducidos son correctos
            try {

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
                    numFilaSeleccionada=tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada,persona);
                }
                TablePosition pos = new TablePosition(tableViewPrevio,numFilaSeleccionada,null);
                tableViewPrevio.getFocusModel().focus(pos);
                tableViewPrevio.requestFocus();

                StackPane rootMain = (StackPane) rootPersonaDetalleView.getScene().getRoot();
                rootMain.getChildren().remove(rootPersonaDetalleView);
                rootAgendaView.setVisible(true);

            } catch (RollbackException ex) { // Los datos introducidos no cumplen requisitos de BD
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. " + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }
        }
    }

    
    public void setRootAgendaView(Pane rootAgendaView){
        this.rootAgendaView = rootAgendaView;
    }
    
    public void setTableViewPrevio(TableView tableViewPrevio){
        this.tableViewPrevio = tableViewPrevio;
    }
    
    // Set persona
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
    
    // Función de mostrar datos en susu celdas correspondientes
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
        
        Query queryProvinciaFindAll=entityManager.createNamedQuery("Provincia.findAll");
        List listProvincia = queryProvinciaFindAll.getResultList();
        rowProvincia.setItems(FXCollections.observableList(listProvincia));
        if (persona.getProvincia() != null){
            rowProvincia.setValue(persona.getProvincia());
        }
        
        rowProvincia.setCellFactory(
        (ListView<Provincia> l)-> new ListCell<Provincia>(){
            @Override
            protected void updateItem(Provincia provincia, boolean empty){
                super.updateItem(provincia, empty);
                if (provincia == null || empty){
                    setText("");
                } else {
                    setText(provincia.getCodigo()+"-"+provincia.getNombre());
                }
                }
        });
        rowProvincia.setConverter(new StringConverter<Provincia>(){
            @Override
            public String toString(Provincia provincia){
            if (provincia == null){
                return null;
            } else {
            return provincia.getCodigo()+"-"+provincia.getNombre();
            }
            }
            @Override
            public Provincia fromString(String userId){
            return null;
            }
        });
        
        if (persona.getFoto() != null){
            String imageFileName=persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            if (file.exists()){
            Image image = new Image(file.toURI().toString());
            imageViewFoto.setImage(image);
        } else {
            Alert alert=new Alert(AlertType.INFORMATION,"No se encuentra la imagen en "+file.toURI().toString());
            alert.showAndWait();
            }
        }

    }
    
    // Boton examinar foto
    @FXML
    private void onActionButtonExaminar(ActionEvent event) {
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()){
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg", "*.png"),
        new FileChooser.ExtensionFilter("Todos los archivos","*.*"));
        File file = fileChooser.showOpenDialog(rootPersonaDetalleView.getScene().getWindow());
        
        if (file != null){
            try {
                Files.copy(file.toPath(),new File(CARPETA_FOTOS+"/"+file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex){
                Alert alert = new Alert(AlertType.WARNING,"Nombre de archivo duplicado");
                alert.showAndWait();
            } catch (IOException ex){
                Alert alert = new Alert(AlertType.WARNING,"No se ha podido guardar la imagen");
                alert.showAndWait();
            }
        }

    }
    
    // Boton suprimir foto
    @FXML
    private void onActionButtonSuprimir(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen, \n"+ "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija la opción deseada:");
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar",
        ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener, 
        buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeEliminar){
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
        if (file.exists()) {
            file.delete();
        }
        persona.setFoto(null);
        imageViewFoto.setImage(null);
        } else if (result.get() == buttonTypeMantener) {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        } 
    }
    
}
