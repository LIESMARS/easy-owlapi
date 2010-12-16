package cz.cvut.owl2query.model;

public enum KBOperation {

    IS_TYPE, 
    IS_DIRECT_TYPE, 
    
    IS_SUBCLASS_OF, 
    IS_DIRECT_SUBCLASS_OF,
    
    IS_EQUIVALENT_CLASS, 
    
    IS_DISJOINT_WITH,

    IS_COMPLEMENT_OF,

    IS_SUBPROPERTY_OF,
    IS_DIRECT_SUBPROPERTY_OF,
	
    IS_EQUIVALENT_PROPERTY, 

    IS_INVERSE_OF,
    
    HAS_PROPERTY_VALUE,
    GET_PROPERTY_VALUE,
    
    GET_INSTANCES,
    GET_DIRECT_INSTANCES,
    
    GET_EQUIVALENT_CLASSES,
	
    GET_DISJOINT_CLASSES,

    GET_EQUIVALENT_PROPERTIES,

    GET_INVERSES
}
