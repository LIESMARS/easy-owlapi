Description
=======

Original at [OWL2Query](https://krizik.felk.cvut.cz/km/owl2query).

Quick Start
=======

1 Compile or download zip: [Binary Release](https://github.com/goshx/easy-owlapi/releases/tag/jars)

2 Import:

```
import org.owlapi.OWL;
```


3 Create:

```
OWL instance = new OWL(owlFilePath);
```


4 Samples:

  a) Read property value of instance:

```
String pvIri       = "Name";           /* property */
String ilIri       = "Building";       /* instance */
Set<String> result = instance.readPropertyValueOfInstance(pvIri, ilIri);
```


  b) Set object value for instance:

```
String iri     = "Building_One";      /* instance */
String key     = "Own";               /* property */
String val     = "A214";              /* another instance */
boolean result = instance.addObjectValueForInstance(iri, key, val);
```

  c) [etc.](https://github.com/goshx/easy-owlapi/blob/master/owl2query-owlapi/src/main/java/org/owlapi/OWLIface.java)
