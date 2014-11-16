package org.owlapi;

import java.util.Set;

public interface OWLIface {

    Set<String> readAllClasses();

    Set<String> readAllDataProperties();

    Set<String> readAllFunctionalProperties();

    Set<String> readAllInstances();
    
    Set<String> readAllInstancesOfClass(String iri);

    Set<String> readAllLiterals();

    Set<String> readAllObjectProperties();
    
    Set<String> readAllSubClasses(String iri);

    Set<String> readAllSubProperties(String iri);

    Set<String> readAllSuperClasses(String iri);

    Set<String> readAllSuperProperties(String iri);

    Set<String> readDomainsOfProperty(String iri);
    
    boolean readExistClass(String iri);
    
    boolean readExistDataProperty(String iri);
    
    boolean readExistInstance(String iri);
    
    boolean readExistObjectProperty(String iri);
    
    boolean readIsDirectSubClass(String subIri, String supIri);
    
    boolean readIsDirectSubProperty(String subIri, String supIri);
    
    boolean readIsSubClass(String subIri, String supIri);
    
    boolean readIsSubProperty(String subIri, String supIri);
    
    Set<String> readDataPropertiesOfInstance(String iri);

    Set<String> readObjectPropertiesOfInstance(String iri);
    
    Set<String> readTypesOfInstance(String iri);

    Set<String> readDirectSubClasses(String iri);

    Set<String> readDirectSubProperties(String iri);

    Set<String> readDirectSuperClasses(String iri);

    Set<String> readDirectSuperProperties(String iri);
    
    Set<String> readPropertyValueOfInstance(String pvIri, String ilIri);
    
    boolean addSubClass(String supIri, String subIri);
    
    boolean addSubDataProperty(String supIri, String subIri);
    
    boolean addSubObjectProperty(String supIri, String subIri);
    
    boolean addInstanceForClass(String iIri, String cIri);
    
    boolean addDataValueForInstance(String iri, String key, String val);
    
    boolean addObjectValueForInstance(String iri, String key, String val);
    
    boolean removeAllSubClasses(String iri);
    
    boolean removeAllSubDataProperties(String iri);
    
    boolean removeAllSubObjectProperties(String iri);
    
    boolean removeClass(String iri);
    
    boolean removeDataProperty(String iri);
    
    boolean removeObjectProperty(String iri);
    
    boolean removeInstance(String iri);
    
    boolean removeTypeOfInstance(String cIri, String iIri);
    
    boolean removePropertyOfInstance(String pIri, String iIri);
    
    boolean removeDataValueOfInstance(String iri, String key, String val);
    
    boolean removeObjectValueOfInstance(String iri, String key, String val);
    
    boolean updateDataValueForInstance(String iri, String key, String before, String after);
    
    boolean updateObjectValueForInstance(String iri, String key, String before, String after);
    
}
