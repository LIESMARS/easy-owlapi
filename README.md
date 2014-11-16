Description
=======

Much easier user interface to use to edit OWL file.

Usage
=======

import:

```
import org.owlapi.OWL;
```

reading sample:

```
public void ReadPropertyValueOfInstance() {
        System.out.println("read property value of instance...");
        String pvIri = "Name";           /* property */
        String ilIri = "Building";       /* instance */
        OWL instance = new OWL(owlFilePath);
        Set<String> result = instance.readPropertyValueOfInstance(pvIri, ilIri);
        System.out.println(String.valueOf(result));
    }
```

writing sample:

```
public void AddObjectValueForInstance() {
        System.out.println("set object value for instance");
        String iri = "Building_One";         /* instance */
        String key = "Own";                  /* property */
        String val = "A214";                 /* another instance */
        OWL instance = new OWL(owlFilePath);
        boolean result = instance.addObjectValueForInstance(iri, key, val);
        System.out.println(String.valueOf(result));
    }
```
