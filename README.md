Description
=======

Much easier user interface to use to edit OWL file.

Usage
=======

Import:

```
import org.owlapi.OWL;
```


Create:

```
OWL instance = new OWL(owlFilePath);
```


Read property value of instance:

```
String pvIri = "Name";           /* property */
String ilIri = "Building";       /* instance */
Set<String> result = instance.readPropertyValueOfInstance(pvIri, ilIri);
```


Set object value for instance:

```
String iri = "Building_One";         /* instance */
String key = "Own";                  /* property */
String val = "A214";                 /* another instance */
boolean result = instance.addObjectValueForInstance(iri, key, val);
```
