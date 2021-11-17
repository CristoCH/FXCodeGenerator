package dad.codegen.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.FXModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController implements Initializable {
	
	// controllers
	
	private BeanController beanController = new BeanController();

	// model
	
	private ObjectProperty<FXModel> fxModel = new SimpleObjectProperty<>();
	private StringProperty packageName = new SimpleStringProperty();
	private ListProperty<Bean> beans = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ObjectProperty<Bean> selectedBean = new SimpleObjectProperty<>();
	
	// view
	
    @FXML
    private GridPane view;

    @FXML
    private Button nuevoButton;

    @FXML
    private Button abrirButton;

    @FXML
    private Button guardarButton;

    @FXML
    private Button generarButton;

    @FXML
    private TextField paqueteText;

    @FXML
    private ListView<Bean> beansList;

    @FXML
    private Button nuevoBeanButton;

    @FXML
    private Button eliminarBeanButton;

    @FXML
    private BorderPane rightPane;

    @FXML
    private VBox noBeanPane;
    
    public MainController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		loader.setController(this);
		loader.load();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// bindings
		
		paqueteText.textProperty().bindBidirectional(packageName);
		beansList.itemsProperty().bind(beans);
		selectedBean.bind(beansList.getSelectionModel().selectedItemProperty());
		
		eliminarBeanButton.disableProperty().bind(selectedBean.isNull());
		
		beanController.beanProperty().bind(selectedBean);
		beanController.beansProperty().bind(beans);

		// listeners
		
		fxModel.addListener((o, ov, nv) -> onFXModelChanged(o, ov, nv));
		selectedBean.addListener((o, ov, nv) -> onSelectedBeanChanged(o, ov, nv));
		
		// inicializamos la app con un modelo vacío
		
		fxModel.set(new FXModel());

	}

	private void onSelectedBeanChanged(ObservableValue<? extends Bean> o, Bean ov, Bean nv) {

		// si hay un bean seleccionado en la lista
		if (nv != null) {
			
			rightPane.setCenter(beanController.getView());
			
		}
		// no hay ningún bean seleccionado
		else {
			
			rightPane.setCenter(noBeanPane);
			
		}
		
	}

	private void onFXModelChanged(ObservableValue<? extends FXModel> o, FXModel ov, FXModel nv) {

		// si había un modelo cargado antes, lo desvinculamos
		if (ov != null) {
			System.out.println("el modelo viejo es: " + ov.getPackageName());
			packageName.unbindBidirectional(ov.packageNameProperty());
			beans.unbind();
		}

		// vinculamos el modelo nuevo
		if (nv != null) {
			System.out.println("el modelo nuevo es: " + nv.getPackageName());
			packageName.bindBidirectional(nv.packageNameProperty());
			beans.bind(nv.beansProperty());
		}
		
	}

	public GridPane getView() {
		return view;
	}

    @FXML
    void onAbrirAction(ActionEvent event) {
    	
    	if (FXCodeGenApp.confirm(
    			"Abrir modelo FX", 
    			"Va a abrir un modelo FX desde fichero.\n\nLos cambios que hata realizado en el modelo actual se perderán.", 
    			"¿Desea continuar?")) {
    		
    		FileChooser fileChooser = new FileChooser();
    		fileChooser.setTitle("Abrir modelo FX");
    		fileChooser.getExtensionFilters().add(new ExtensionFilter("Modelo FX", "*.fx"));
    		fileChooser.getExtensionFilters().add(new ExtensionFilter("Todos los archivos", "*.*"));
    		fileChooser.setInitialDirectory(new File("."));
    		File fichero = fileChooser.showOpenDialog(FXCodeGenApp.getPrimaryStage());
    		if (fichero != null) {
    			
    			try {
					fxModel.set(FXModel.load(fichero));
				} catch (Exception e) {
					e.printStackTrace();
					FXCodeGenApp.error("Error al abrir el modelo FX desde el fichero '" + fichero.getName() + "'.", e); 
				}
    			
    		}
    		
    	}
    	
    }

    @FXML
    void onEliminarBeanAction(ActionEvent event) {
    	
    	if (FXCodeGenApp.confirm(
    			"Eliminar bean", 
    			"Se va a eliminar el bean '" + selectedBean.get().getName() + "'", 
    			"¿Desea continuar?")) {
    		
    		fxModel.get().getBeans().remove(selectedBean.get());
    		
    	}
    	
    }

    @FXML
    void onGenerarAction(ActionEvent event) {

    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.setTitle("Seleccionar carpeta para generar código");
    	dirChooser.setInitialDirectory(new File("."));
    	File directorio = dirChooser.showDialog(FXCodeGenApp.getPrimaryStage());
    	if (directorio != null) {
    		
    		try {
        		fxModel.get().generateCode(directorio);
        		FXCodeGenApp.info("Se ha generado el código correctamente en el directorio '" + directorio.getName() + "'.");
			} catch (IOException e) {
				e.printStackTrace();
				FXCodeGenApp.error("Error al generar el código del modelo FX en el directorio '" + directorio.getName() + "'.", e);
			}
    		
    	}

    }

    @FXML
    void onGuardarAction(ActionEvent event) {
    	
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Guardar modelo FX");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Modelo FX", "*.fx"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Todos los archivos", "*.*"));
		fileChooser.setInitialDirectory(new File("."));
		File fichero = fileChooser.showSaveDialog(FXCodeGenApp.getPrimaryStage());
		if (fichero != null) {
			
			try {
				fxModel.get().save(fichero);
			} catch (Exception e) {
				e.printStackTrace();
				FXCodeGenApp.error("Error al guardar el modelo FX en el fichero '" + fichero.getName() + "'.", e); 
			}
			
		}
    	
    }

    @FXML
    void onNuevoAction(ActionEvent event) {
    	if (FXCodeGenApp.confirm(
    			"Nuevo modelo FX", 
    			"Se va a crear un nuevo modelo FX.\n\nLos cambios que hata realizado en el modelo actual se perderán.", 
    			"¿Desea continuar?")) {
    		
    		fxModel.set(new FXModel());
    		
    	}
    }

    @FXML
    void onNuevoBeanAction(ActionEvent event) {

    	Bean nuevo = new Bean();
    	nuevo.setName("NuevoBean");
    	
    	fxModel.get().getBeans().add(nuevo);
    	
    	beansList.getSelectionModel().select(nuevo);
    
    }
    
}
