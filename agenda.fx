<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<fxModel packageName="dad.agenda.model">
    <bean name="Telefono">
        <property name="id" readOnly="false" type="LONG"/>
        <property name="numero" readOnly="false" type="STRING"/>
    </bean>
    <bean name="Persona">
        <property name="nombre" readOnly="false" type="STRING"/>
        <property name="apellidos" readOnly="false" type="STRING"/>
        <property name="fechaNacimiento" readOnly="false" type="DATE"/>
        <property name="edad" readOnly="true" type="INTEGER"/>
        <property generic="Telefono" name="telefonos" readOnly="false" type="LIST"/>
    </bean>
    <bean name="Alumno" parent="Persona">
        <property name="cial" readOnly="false" type="STRING"/>
    </bean>
</fxModel>
