package dad.codegen;

import java.io.File;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.FXModel;
import dad.codegen.model.javafx.Property;
import dad.codegen.model.javafx.Type;

public class Ejemplo {

	public static void main(String[] args) throws Exception {

		Property real = new Property();
		real.setName("real");
		real.setType(Type.DOUBLE);

		Property imaginario = new Property();
		imaginario.setName("imaginario");
		imaginario.setType(Type.DOUBLE);
		
		Bean complejo = new Bean();
		complejo.setName("Complejo");
		complejo.getProperties().addAll(real, imaginario);
		
		FXModel model = new FXModel();
		model.setPackageName("dad.calculadora.compleja");
		model.getBeans().add(complejo);
		
		model.generateCode(new File("gen"));
		
		model.save(new File("complejo.fx"));
	}

}
