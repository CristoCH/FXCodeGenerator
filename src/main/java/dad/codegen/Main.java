package dad.codegen;

import java.io.File;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.FXModel;
import dad.codegen.model.javafx.Property;
import dad.codegen.model.javafx.Type;

public class Main {

	public static void main(String[] args) throws Exception {
		
		// -------------------------------------------
		// crea el bean FX "Telefono" con las
		// propiedades: id, numero
		
		Property idTelefono = new Property();
		idTelefono.setName("id");
		idTelefono.setType(Type.LONG);

		Property numero = new Property();
		numero.setName("numero");
		numero.setType(Type.STRING);
		
		Bean telefono = new Bean();
		telefono.setName("Telefono");
		telefono.getProperties().addAll(idTelefono, numero);

		// -------------------------------------------
		// crea el bean FX "Persona" con las 
		// propiedades: nombre, apellidos y telefonos
		
		Property nombre = new Property();
		nombre.setName("nombre");
		nombre.setType(Type.STRING);
		
		Property apellidos = new Property();
		apellidos.setName("apellidos");
		apellidos.setType(Type.STRING);
		
		Property fechaNacimiento = new Property();
		fechaNacimiento.setName("fechaNacimiento");
		fechaNacimiento.setType(Type.DATE);
		
		Property edad = new Property();
		edad.setName("edad");
		edad.setType(Type.INTEGER);
		edad.setReadOnly(true);

		Property telefonos = new Property();
		telefonos.setName("telefonos");
		telefonos.setType(Type.LIST);
		telefonos.setGeneric(telefono);
	
		Bean persona = new Bean();
		persona.setName("Persona");
		persona.getProperties().addAll(nombre, apellidos, fechaNacimiento, edad, telefonos);

		// -------------------------------------------
		// crea el bean FX "Contacto" con las 
		// propiedades: nombre, apellidos y telefonos

		Property cial = new Property();
		cial.setName("cial");
		cial.setType(Type.STRING);

		Bean alumno = new Bean();
		alumno.setName("Alumno");
		alumno.setParent(persona);
		alumno.getProperties().addAll(cial);
		
		// -------------------------------------------
		// crea el modelo FX y le añade los beans: Telefono 
		// y Persona
		
		FXModel model = new FXModel();
		model.setPackageName("dad.agenda.model");
		model.getBeans().addAll(telefono, persona, alumno);
		
		// -------------------------------------------
		// genera el código de los beans FX en el directorio "gen"
		// - crea los paquetes necesario y dentro los ficheros ".java" 
		// de cada bean FX
		model.generateCode(new File("gen"));

		// -------------------------------------------
		// genera el código fuente del bean "Persona" en un String y lo muestra
		// NOTA: el bean debe haber sido añadido al modelo previamente
		String codigo = model.generateBeanCode(alumno);
		System.out.println(codigo);

		// -------------------------------------------
		// guarda el modelo en XML
		model.save(new File("agenda.fx"));
		
	}

}
