package org.owlapi;

import cz.cvut.kbss.owl2query.model.OWLObjectType;
import java.util.HashSet;
import org.owlapi.impl.OWLAPIOntology;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLProperty;

/**
 * 
 * @author GYM
 */
public class OWL implements OWLIface {
    
    private final OWLAPIOntology o;
    
    public OWL(String owlFilePath){
        o = new OWLAPIOntology(owlFilePath);
    }
    
    private Set<String> asStringSet(Set<? extends OWLObject> e) {
        Set<String> set = new HashSet();
        for (OWLObject next : e) {
            String n = next.toString();
            if(next.equals(o.getFactory().getNothing()))
                continue;
            set.add(n);
        }
        return set;
    }
    
    @Override
    public Set<String> readAllClasses() {
        Set<? extends OWLObject> set = o.getClasses();
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readAllInstances() {
        Set<? extends OWLObject> set = o.getIndividuals();
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readAllObjectProperties() {
        Set<? extends OWLObject> set = o.getObjectProperties();
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readAllDataProperties() {
        Set<? extends OWLObject> set = o.getDataProperties();
        return asStringSet(set);
    }

    @Override
    public Set<String> readAllFunctionalProperties() {
        Set<? extends OWLObject> set = o.getFunctionalProperties();
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readAllLiterals() {
        Set<? extends OWLObject> set = o.getLiterals();
        return asStringSet(set);
    }

    @Override
    public Set<String> readAllInstancesOfClass(String iri) {
        OWLObject i = o.asOWLObject(iri,OWLObjectType.OWLClass);
        return asStringSet(o.getInstances(i, true));
    }
    
    @Override
    public Set<String> readDomainsOfProperty(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getDomains(i);
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readDataPropertiesOfInstance(String iri) {
        OWLObject i = o.asOWLObject(iri,OWLObjectType.OWLNamedIndividual);
        Set<? extends OWLObject> set = o.getPropertiesForOWLObject(i, 
                OWLObjectType.OWLDataProperty);
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readObjectPropertiesOfInstance(String iri) {
        OWLObject i = o.asOWLObject(iri,OWLObjectType.OWLNamedIndividual);
        Set<? extends OWLObject> set = o.getPropertiesForOWLObject(i, 
                OWLObjectType.OWLObjectProperty);
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readTypesOfInstance(String iri) {
        OWLObject i = o.asOWLObject(iri,OWLObjectType.OWLNamedIndividual);
        Set<? extends OWLObject> set = o.getPropertiesForOWLObject(i, 
                OWLObjectType.OWLClass);
        return asStringSet(set);
    }

    @Override
    public Set<String> readAllSubClasses(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getClassHierarchy().getSubs(i, false);
        return asStringSet(set);
    }

    @Override
    public Set<String> readAllSubProperties(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getPropertyHierarchy().getSubs(i, false);
        return asStringSet(set);
    }

    @Override
    public Set<String> readAllSuperClasses(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getClassHierarchy().getSupers(i, false);
        return asStringSet(set);
    }

    @Override
    public Set<String> readAllSuperProperties(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getPropertyHierarchy().getSupers(i, false);
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readDirectSuperClasses(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getClassHierarchy().getSupers(i, true);
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readDirectSubClasses(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getClassHierarchy().getSubs(i, true);
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readDirectSuperProperties(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getPropertyHierarchy().getSupers(i, true);
        return asStringSet(set);
    }
    
    @Override
    public Set<String> readDirectSubProperties(String iri) {
        OWLObject i = o.asOWLObject(iri);
        Set<? extends OWLObject> set = o.getPropertyHierarchy().getSubs(i, true);
        return asStringSet(set);
    }

    @Override
    public boolean readExistClass(String iri) {
        OWLObject i = o.asOWLObject(iri, OWLObjectType.OWLClass);
        return o.is(i, OWLObjectType.OWLClass);
    }

    @Override
    public boolean readExistDataProperty(String iri) {
        OWLObject i = o.asOWLObject(iri, OWLObjectType.OWLDataProperty);
        return o.is(i, OWLObjectType.OWLDataProperty);
    }

    @Override
    public boolean readExistObjectProperty(String iri) {
        OWLObject i = o.asOWLObject(iri, OWLObjectType.OWLObjectProperty);
        return o.is(i, OWLObjectType.OWLObjectProperty); 
    }

    @Override
    public boolean readExistInstance(String iri) {
        OWLObject i = o.asOWLObject(iri, OWLObjectType.OWLNamedIndividual);
        return o.is(i, OWLObjectType.OWLNamedIndividual);
    }

    @Override
    public boolean readIsDirectSubClass(String subIri, String supIri) {
        OWLObject sub = o.asOWLObject(subIri);
        OWLObject sup = o.asOWLObject(supIri);
        return o.getClassHierarchy().isSub(sub, sup, true);
    }

    @Override
    public boolean readIsDirectSubProperty(String subIri, String supIri) {
        OWLObject sub = o.asOWLObject(subIri);
        OWLObject sup = o.asOWLObject(supIri);
        return o.getPropertyHierarchy().isSub(sub, sup, true);
    }

    @Override
    public boolean readIsSubClass(String subIri, String supIri) {
        OWLObject sub = o.asOWLObject(subIri);
        OWLObject sup = o.asOWLObject(supIri);
        return o.getClassHierarchy().isSub(sub, sup, false);
    }

    @Override
    public boolean readIsSubProperty(String subIri, String supIri) {
        OWLObject sub = o.asOWLObject(subIri);
        OWLObject sup = o.asOWLObject(supIri);
        return o.getPropertyHierarchy().isSub(sub, sup, false);
    }
    
    @Override
    public Set<String> readPropertyValueOfInstance(String pvIri, String ilIri) {
        OWLObject p = o.asOWLObject(pvIri);
        OWLObject i = o.asOWLObject(ilIri, OWLObjectType.OWLNamedIndividual);
        return asStringSet(o.getPropertyValues(p, i));
    }

    @Override
    public boolean addSubClass(String supIri, String subIri) {
        OWLObject sub = o.asOWLObject(subIri, OWLObjectType.OWLClass);
        OWLObject sup = o.asOWLObject(supIri, OWLObjectType.OWLClass);
        return o.setSubClass(sub, sup);
    }

    @Override
    public boolean addSubDataProperty(String supIri, String subIri) {
        OWLObject sub = o.asOWLObject(subIri,OWLObjectType.OWLDataProperty);
        OWLObject sup = o.asOWLObject(supIri,OWLObjectType.OWLDataProperty);
        return o.setSubDataProperty(sub, sup);
    }

    @Override
    public boolean addSubObjectProperty(String supIri, String subIri) {
        OWLObject sub = o.asOWLObject(subIri,OWLObjectType.OWLObjectProperty);
        OWLObject sup = o.asOWLObject(supIri,OWLObjectType.OWLObjectProperty);
        return o.setSubObjectProperty(sub, sup);
    }

    @Override
    public boolean addInstanceForClass(String iIri, String cIri) {
        OWLObject i = o.asOWLObject(iIri,OWLObjectType.OWLNamedIndividual);
        OWLObject c = o.asOWLObject(cIri);
        return o.setIndividualForClass(i, c);
    }

    @Override
    public boolean addDataValueForInstance(String iri, String key, String val) {
        OWLObject i = o.asOWLObject(iri,OWLObjectType.OWLNamedIndividual);
        OWLObject k = o.asOWLObject(key, OWLObjectType.OWLDataProperty);
        OWLObject v = o.asOWLObject(val, OWLObjectType.OWLLiteral);
        return o.setPropertyValueForIndividual(i, k, v);
    }

    @Override
    public boolean addObjectValueForInstance(String iri, String key, String val) {
        OWLObject i = o.asOWLObject(iri,OWLObjectType.OWLNamedIndividual);
        OWLObject k = o.asOWLObject(key, OWLObjectType.OWLObjectProperty);
        OWLObject v = o.asOWLObject(val, OWLObjectType.OWLNamedIndividual);
        return o.setPropertyValueForIndividual(i, k, v);
    }

    @Override
    public boolean removeAllSubClasses(String iri) {
        for (OWLClass next : o.getClassHierarchy()
                .getSubs((OWLClass)o.asOWLObject(iri), false)) {
            o.removeOWLClass(next);
        }
        return true;
    }

    @Override
    public boolean removeAllSubDataProperties(String iri) {
        for (OWLProperty next : o.getPropertyHierarchy()
                .getSubs(o.asOWLObject(iri,OWLObjectType.OWLDataProperty), false)) {
            o.removeOWLDataProperty(next);
        }
        return true;
    }
    
    @Override
    public boolean removeAllSubObjectProperties(String iri) {
        for (OWLProperty next : o.getPropertyHierarchy()
                .getSubs(o.asOWLObject(iri,OWLObjectType.OWLObjectProperty), false)) {
            o.removeOWLObjectProperty(next);
        }
        return true;
    }

    @Override
    public boolean removeClass(String iri) {
        OWLObject e = o.asOWLObject(iri, OWLObjectType.OWLClass);
        return o.removeOWLClass(e);
    }

    @Override
    public boolean removeTypeOfInstance(String tIri, String iIri) {
        OWLObject i = o.asOWLObject(iIri,OWLObjectType.OWLNamedIndividual);
        OWLObject c = o.asOWLObject(tIri,OWLObjectType.OWLClass);
        return o.removeTypeOfIndividual(c, i);
    }

    @Override
    public boolean removeDataProperty(String iri) {
        OWLObject e = o.asOWLObject(iri, OWLObjectType.OWLDataProperty);
        return o.removeOWLDataProperty(e);
    }

    @Override
    public boolean removeObjectProperty(String iri) {
        OWLObject e = o.asOWLObject(iri, OWLObjectType.OWLObjectProperty);
        return o.removeOWLObjectProperty(e);
    }

    @Override
    public boolean removeInstance(String iri) {
        OWLObject e = o.asOWLObject(iri, OWLObjectType.OWLNamedIndividual);
        return o.removeOWLIndividual(e);
    }

    @Override
    public boolean removePropertyOfInstance(String pIri, String iIri) {
        for(String v: readPropertyValueOfInstance(pIri, iIri)) {
            removeObjectValueOfInstance(iIri, pIri, v);
        }
        return true;
    }

    @Override
    public boolean removeDataValueOfInstance(String iIri, String pIri, String val) {
        OWLObject i = o.asOWLObject(iIri,OWLObjectType.OWLNamedIndividual);
        OWLObject c = o.asOWLObject(pIri,OWLObjectType.OWLDataProperty);
        OWLObject v = o.asOWLObject(val, OWLObjectType.OWLLiteral);
        return o.removePropertyForIndividual(i, c, v);
    }
    
    @Override
    public boolean removeObjectValueOfInstance(String iIri, String pIri, String val) {
        OWLObject i = o.asOWLObject(iIri,OWLObjectType.OWLNamedIndividual);
        OWLObject c = o.asOWLObject(pIri,OWLObjectType.OWLObjectProperty);
        OWLObject v = o.asOWLObject(val, OWLObjectType.OWLNamedIndividual);
        return o.removePropertyForIndividual(i, c, v);
    }

    @Override
    public boolean updateDataValueForInstance(String iri, String key, String before, String after) {
        if(removeDataValueOfInstance(iri, key, before))
            return addDataValueForInstance(iri, key, after);
        return false;
    }

    @Override
    public boolean updateObjectValueForInstance(String iri, String key, String before, String after) {
        if(removeObjectValueOfInstance(iri, key, before))
            return addObjectValueForInstance(iri, key, after);
        return false;
    }
}
