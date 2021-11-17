package dad.codegen.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.Property;
import dad.codegen.model.javafx.Type;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class EditBeanController implements Initializable {
	
	private Stage stage;

	// model (interno)

	private StringProperty name = new SimpleStringProperty();
	private BooleanProperty readOnly = new SimpleBooleanProperty();
	private ObjectProperty<Type> type = new SimpleObjectProperty<>();
	private ObjectProperty<Bean> generic = new SimpleObjectProperty<>();

	// model (externo)

	private ObjectProperty<Property> property = new SimpleObjectProperty<>();
	private ListProperty<Bean> beans = new SimpleListProperty<>(FXCollections.observableArrayList());

	// view

	@FXML
	private GridPane view;

	@FXML
	private Button backButton;

	@FXML
	private TextField nameText;

	@FXML
	private CheckBox readOnlyCheck;

	@FXML
	private ComboBox<Type> typeCombo;

	@FXML
	private ComboBox<Bean> genericCombo;
	
	public EditBeanController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditBeanView.fxml"));
		loader.setController(this);
		loader.load();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// bindings
		
		nameText.textProperty().bindBidirectional(name);
		readOnlyCheck.selectedProperty().bindBidirectional(readOnly);
		typeCombo.valueProperty().bindBidirectional(type);
		typeCombo.getItems().addAll(Type.values());
		genericCombo.valueProperty().bindBidirectional(generic);
		genericCombo.itemsProperty().bind(beans);

		// crea el stage para mostrar la vista de este controlador
		
		stage = new Stage();
		stage.initOwner(FXCodeGenApp.getPrimaryStage());
		stage.setTitle("Editar propiedad");
		stage.setScene(new Scene(getView()));
		
		// listeners
		
		property.addListener((o, ov, nv) -> onPropertyChanged(o, ov, nv));
		
	}
	
	private void onPropertyChanged(ObservableValue<? extends Property> o, Property ov, Property nv) {
		
		if (ov != null) {
			name.unbindBidirectional(ov.nameProperty());
			readOnly.unbindBidirectional(ov.readOnlyProperty());
			type.unbindBidirectional(ov.typeProperty());
			generic.unbindBidirectional(ov.genericProperty());
		}
		
		if (nv != null) {
			name.bindBidirectional(nv.nameProperty());
			readOnly.bindBidirectional(nv.readOnlyProperty());
			type.bindBidirectional(nv.typeProperty());
			generic.bindBidirectional(nv.genericProperty());			
		}
		
	}

	public void show(Property property) {
		this.property.set(property);
		stage.showAndWait();
	}

	public GridPane getView() {
		return view;
	}

	@FXML
	void onBackAction(ActionEvent event) {
		stage.close();
	}

	// getters y setters

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
