package dad.codegen.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.Property;
import dad.codegen.model.javafx.Type;
import javafx.fxml.Initializable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;

public class BeanController implements Initializable {
	
	// controllers 
	
	private EditBeanController editBeanController = new EditBeanController();

	// model (interno)

	private StringProperty name = new SimpleStringProperty();
	private ObjectProperty<Bean> parent = new SimpleObjectProperty<>();
	private ListProperty<Property> properties = new SimpleListProperty<>(FXCollections.observableArrayList());
	private ObjectProperty<Property> selectedProperty = new SimpleObjectProperty<>();

	// model (externo)

	private ObjectProperty<Bean> bean = new SimpleObjectProperty<>();
	private ListProperty<Bean> beans = new SimpleListProperty<>(FXCollections.observableArrayList());

	// view

	@FXML
	private GridPane view;

	@FXML
	private TextField nombreText;

	@FXML
	private ComboBox<Bean> padreCombo;

	@FXML
	private Button quitarPadreButton;

	@FXML
	private Button nuevaPropiedadButton;

	@FXML
	private Button eliminarPropiedadButton;

	@FXML
	private Button editarPropiedadButton;

	@FXML
	private TableView<Property> propiedadesTable;

	@FXML
	private TableColumn<Property, String> nombreColumn;

	@FXML
	private TableColumn<Property, Boolean> soloLecturaColumn;

	@FXML
	private TableColumn<Property, Type> tipoColumn;

	@FXML
	private TableColumn<Property, Bean> genericoColumn;

	public BeanController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BeanView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// bindings
		
		selectedProperty.bind(propiedadesTable.getSelectionModel().selectedItemProperty());
		
		nombreText.textProperty().bindBidirectional(name);
		padreCombo.valueProperty().bindBidirectional(parent);
		padreCombo.itemsProperty().bind(beans);
		propiedadesTable.itemsProperty().bind(properties);
		
		quitarPadreButton.disableProperty().bind(parent.isNull());
		editarPropiedadButton.disableProperty().bind(selectedProperty.isNull());
		eliminarPropiedadButton.disableProperty().bind(selectedProperty.isNull());
		
		editBeanController.beansProperty().bind(beans);
		
		// cell value factories
		
		nombreColumn.setCellValueFactory(v -> v.getValue().nameProperty());
		soloLecturaColumn.setCellValueFactory(v -> v.getValue().readOnlyProperty());
		tipoColumn.setCellValueFactory(v -> v.getValue().typeProperty());
		genericoColumn.setCellValueFactory(v -> v.getValue().genericProperty());
		
		// cell factories
		
		soloLecturaColumn.setCellFactory(CheckBoxTableCell.forTableColumn(soloLecturaColumn));
		
		// listener
		
		bean.addListener((o, ov, nv) -> onBeanChanged(o, ov, nv));

	}

	private void onBeanChanged(ObservableValue<? extends Bean> o, Bean ov, Bean nv) {

		if (ov != null) {
			// desbindeo del bean viejo
			name.unbindBidirectional(ov.nameProperty());
			parent.unbindBidirectional(ov.parentProperty());
			properties.unbind();
		}
		
		if (nv != null) {
			// bindeo del bean nuevo
			name.bindBidirectional(nv.nameProperty());
			parent.bindBidirectional(nv.parentProperty());
			properties.bind(nv.propertiesProperty());
		}
		
	}

	public GridPane getView() {
		return view;
	}

	@FXML
	void onEditarPropiedadAction(ActionEvent event) {

		editBeanController.show(selectedProperty.get());
		
	}

	@FXML
	void onEliminarPropiedadAction(ActionEvent event) {

		Property selected = selectedProperty.get();
		if (FXCodeGenApp.confirm(
				"Eliminar propiedad", 
				"Se va a eliminar la propiedad '" + selected.getName() + "'.", 
				"¿Desea continuar?")) {
			
			properties.remove(selected);
			
		}
		
	}

	@FXML
	void onNuevaPropiedadAction(ActionEvent event) {

		Property property = new Property();
		property.setName("nuevaPropiedad");
		property.setType(Type.STRING);
		property.setGeneric(null);
		property.setReadOnly(false);
		
		properties.add(property);
		
		propiedadesTable.getSelectionModel().select(property);
		
		editBeanController.show(property);
		
	}

	@FXML
	void onQuitarPadreAction(ActionEvent event) {
		parent.set(null);
	}

	public ObjectProperty<Bean> beanProperty() {
		return this.bean;
	}

	// getters y setters

	public Bean getBean() {
		return this.beanProperty().get();
	}

	public void setBean(final Bean bean) {
		this.beanProperty().set(bean);
	}

	public ListProperty<Bean> beansProperty() {
		return this.beans;
	}

	public ObservableList<Bean> getBeans() {
		return this.beansProperty().get();
	}

	public void setBeans(final ObservableList<Bean> beans) {
		this.beansProperty().set(beans);
	}

}
