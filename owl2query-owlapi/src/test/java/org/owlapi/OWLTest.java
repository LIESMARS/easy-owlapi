/*
 * Unit Test For OWL.java
 * 单元测试/接口示例
 */
package org.owlapi;

import java.util.Set;
import junit.framework.TestCase;

public class OWLTest extends TestCase {
    
    /* OWL文件路径 */
    public final String owlFilePath = "ontology/building.owl"; 
    
    public OWLTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
	
    ///////////////////////////////////////////////////////////////////////
    //                            Reading                                //
    ///////////////////////////////////////////////////////////////////////

    /**
     * Test of readAllClasses method, of class OWL.
     */
    public void testReadAllClasses() {
        System.out.println("获取所有的类");
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllClasses();
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllInstances method, of class OWL.
     */
    public void testReadAllInstances() {
        System.out.println("获取所有实例");
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllInstances();
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllObjectProperties method, of class OWL.
     */
    public void testReadAllObjectProperties() {
        System.out.println("获取所有的对象属性");
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllObjectProperties();
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllDataProperties method, of class OWL.
     */
    public void testReadAllDataProperties() {
        System.out.println("获取所有数据属性");
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllDataProperties();
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllFunctionalProperties method, of class OWL.
     */
    public void testReadAllFunctionalProperties() {
        System.out.println("获取所有功能性属性");
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllFunctionalProperties();
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllLiterals method, of class OWL.
     */
    public void testReadAllLiterals() {
        System.out.println("获取所有的字符串");
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllLiterals();
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllInstancesOfClass method, of class OWL.
     */
    public void testReadAllInstancesOfClass() {
        System.out.println("读取该类的所有实例");
        String iri = "Teacher";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllInstancesOfClass(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readDomainsOfProperty method, of class OWL.
     */
    public void testReadDomainsOfProperty() {
        System.out.println("获取该属性的作用领域");
        String iri = "Name";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readDomainsOfProperty(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readDataPropertiesOfInstance method, of class OWL.
     */
    public void testReadDataPropertiesOfInstance() {
        System.out.println("获取该实例的所有数据属性");
        String iri = "二楼";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readDataPropertiesOfInstance(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readObjectPropertiesOfInstance method, of class OWL.
     */
    public void testReadObjectPropertiesOfInstance() {
        System.out.println("获取该实例的所有对象属性");
        String iri = "二楼";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readObjectPropertiesOfInstance(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readTypesOfInstance method, of class OWL.
     */
    public void testReadTypesOfInstance() {
        System.out.println("获取该实例的类别");
        String iri = "二楼";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readTypesOfInstance(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllSubClasses method, of class OWL.
     */
    public void testReadAllSubClasses() {
        System.out.println("获取该类的所有继承子类");
        String iri = "Container";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllSubClasses(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllSubProperties method, of class OWL.
     */
    public void testReadAllSubProperties() {
        System.out.println("获取该属性的所有继承属性");
        String iri = "Name";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllSubProperties(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllSuperClasses method, of class OWL.
     */
    public void testReadAllSuperClasses() {
        System.out.println("获取该类的所有父类");
        String iri = "Container";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllSuperClasses(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readAllSuperProperties method, of class OWL.
     */
    public void testReadAllSuperProperties() {
        System.out.println("获取该属性的所有父属性");
        String iri = "Name";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readAllSuperProperties(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readDirectSuperClasses method, of class OWL.
     */
    public void testReadDirectSuperClasses() {
        System.out.println("获取该类的所有直接父类");
        String iri = "Container";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readDirectSuperClasses(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readDirectSubClasses method, of class OWL.
     */
    public void testReadDirectSubClasses() {
        System.out.println("获取该类的所有直接子类");
        String iri = "Container";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readDirectSubClasses(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readDirectSuperProperties method, of class OWL.
     */
    public void testReadDirectSuperProperties() {
        System.out.println("获取该属性的直接父属性");
        String iri = "Name";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readDirectSuperProperties(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readDirectSubProperties method, of class OWL.
     */
    public void testReadDirectSubProperties() {
        System.out.println("获取该属性的直接子属性");
        String iri = "Name";
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readDirectSubProperties(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readExistClass method, of class OWL.
     */
    public void testReadExistClass() {
        System.out.println("判断是否存在该类");
        String iri = "Container";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readExistClass(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readExistDataProperty method, of class OWL.
     */
    public void testReadExistDataProperty() {
        System.out.println("判断是否存在该数据属性");
        String iri = "Name";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readExistDataProperty(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readExistObjectProperty method, of class OWL.
     */
    public void testReadExistObjectProperty() {
        System.out.println("判断是否存在该对象属性");
        String iri = "Own";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readExistObjectProperty(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readExistInstance method, of class OWL.
     */
    public void testReadExistInstance() {
        System.out.println("判断是否存在该实例");
        String iri = "二楼";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readExistInstance(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readIsDirectSubClass method, of class OWL.
     */
    public void testReadIsDirectSubClass() {
        System.out.println("判断一个类是否直接继承另一个类");
        String subIri = "Room";      /* 子类 */
        String supIri = "Container"; /* 父类 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readIsDirectSubClass(subIri, supIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readIsDirectSubProperty method, of class OWL.
     */
    public void testReadIsDirectSubProperty() {
        System.out.println("判断一个属性是否直接继承另一个属性");
        String subIri = "Name";      /* 子属性 */
        String supIri = "Type";      /* 父属性 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readIsDirectSubProperty(subIri, supIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readIsSubClass method, of class OWL.
     */
    public void testReadIsSubClass() {
        System.out.println("判断一个类是否继承另一个类");
        String subIri = "Room";      /* 子类 */
        String supIri = "Container"; /* 父类 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readIsSubClass(subIri, supIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readIsSubProperty method, of class OWL.
     */
    public void testReadIsSubProperty() {
        System.out.println("判断一个属性是否直接继承另一个属性");
        String subIri = "Name";      /* 子属性 */
        String supIri = "Type";      /* 父属性 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.readIsSubProperty(subIri, supIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of readPropertyValueOfInstance method, of class OWL.
     */
    public void testReadPropertyValueOfInstance() {
        System.out.println("获取一个实例的属性值");
        String pvIri = "Name";       /* 属性 */
        String ilIri = "二楼";       /* 实例 */
        OWL instance = new OWL(owlFilePath);
        
        Set<String> result = instance.readPropertyValueOfInstance(pvIri, ilIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
        fail(String.valueOf(result.toArray()[0]));
    }
	
    ///////////////////////////////////////////////////////////////////////
    //                            Writing                                //
    ///////////////////////////////////////////////////////////////////////

    /**
     * Test of addSubClass method, of class OWL.
     */
    public void testAddSubClass() {
        System.out.println("为该类添加一个子类");
        String supIri = "Room";      /* 父类 */
        String subIri = "SmallRoom"; /* 子类 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.addSubClass(supIri, subIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of addSubDataProperty method, of class OWL.
     */
    public void testAddSubDataProperty() {
        System.out.println("为该数据属性添加一个子属性");
        String supIri = "Name";      /* 父属性 */
        String subIri = "NickName";  /* 子属性 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.addSubDataProperty(supIri, subIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of addSubObjectProperty method, of class OWL.
     */
    public void testAddSubObjectProperty() {
        System.out.println("为该对象属性添加一个子属性");
        String supIri = "Own";       /* 父属性 */
        String subIri = "SomeWhat";  /* 子属性 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.addSubObjectProperty(supIri, subIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of addInstanceForClass method, of class OWL.
     */
    public void testAddInstanceForClass() {
        System.out.println("为该类添加一个实例");
        String iIri = "Room";        /* 类 */
        String cIri = "A214";        /* 属性 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.addInstanceForClass(iIri, cIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of addDataValueForInstance method, of class OWL.
     */
    public void testAddDataValueForInstance() {
        System.out.println("为该实例添加一个数据属性并赋值");
        String iri = "A214";         /* 实例 */
        String key = "Name";         /* 属性 */
        String val = "TestVal";      /* 属性值 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.addDataValueForInstance(iri, key, val);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of addObjectValueForInstance method, of class OWL.
     */
    public void testAddObjectValueForInstance() {
        System.out.println("为该实例添加一个对象属性并赋值");
        String iri = "二楼";         /* 实例 */
        String key = "Own";          /* 属性 */
        String val = "A214";         /* 另一个实例 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.addObjectValueForInstance(iri, key, val);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeAllSubClasses method, of class OWL.
     */
    public void testRemoveAllSubClasses() {
        System.out.println("删除该类下的所有子类");
        String iri = "Room";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeAllSubClasses(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeAllSubDataProperties method, of class OWL.
     */
    public void testRemoveAllSubDataProperties() {
        System.out.println("删除该数据属性下的所有子属性");
        String iri = "Name";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeAllSubDataProperties(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeAllSubObjectProperties method, of class OWL.
     */
    public void testRemoveAllSubObjectProperties() {
        System.out.println("删除该对象属性下的所有子对象");
        String iri = "Own";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeAllSubObjectProperties(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeClass method, of class OWL.
     */
    public void testRemoveClass() {
        System.out.println("删除该类");
        String iri = "SmallRoom";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeClass(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeTypeOfInstance method, of class OWL.
     */
    public void testRemoveTypeOfInstance() {
        System.out.println("移除该实例的一个类别");
        String tIri = "Room";       /* 类 */
        String iIri = "A214";       /* 实例 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeTypeOfInstance(tIri, iIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeDataProperty method, of class OWL.
     */
    public void testRemoveDataProperty() {
        System.out.println("删除一个数据属性");
        String iri = "NickName";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeDataProperty(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeObjectProperty method, of class OWL.
     */
    public void testRemoveObjectProperty() {
        System.out.println("删除一个对象属性");
        String iri = "SomeWhat";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeObjectProperty(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeInstance method, of class OWL.
     */
    public void testRemoveInstance() {
        System.out.println("删除该实例");
        String iri = "A214";
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeInstance(iri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removePropertyOfInstance method, of class OWL.
     */
    public void testRemovePropertyOfInstance() {
        System.out.println("删除该实例的一个属性");
        String pIri = "Own";       /* 属性 */
        String iIri = "二楼";      /* 实例 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removePropertyOfInstance(pIri, iIri);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeDataValueOfInstance method, of class OWL.
     */
    public void testRemoveDataValueOfInstance() {
        System.out.println("删除该实例的一个数据属性值");
        String iIri = "A214";       /* 实例 */
        String pIri = "Name";       /* 属性 */
        String val = "TestVal";     /* 属性值 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeDataValueOfInstance(iIri, pIri, val);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of removeObjectValueOfInstance method, of class OWL.
     */
    public void testRemoveObjectValueOfInstance() {
        System.out.println("删除该实例的一个对象属性值");
        String iIri = "A214";        /* 实例 */
        String pIri = "Own";         /* 属性 */
        String val = "Nothing";      /* 属性值 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.removeObjectValueOfInstance(iIri, pIri, val);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of updateDataValueForInstance method, of class OWL.
     */
    public void testUpdateDataValueForInstance() {
        System.out.println("更新该实例的一个数据属性值");
        String iri = "二楼";        /* 实例 */
        String key = "Name";        /* 属性 */
        String before = "二楼";     /* 原来的属性值 */
        String after = "二楼";      /* 更新后的属性值 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.updateDataValueForInstance(iri, key, before, after);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }

    /**
     * Test of updateObjectValueForInstance method, of class OWL.
     */
    public void testUpdateObjectValueForInstance() {
        System.out.println("更新该实例的一个对象属性值");
        String iri = "二楼";                     /* 实例 */
        String key = "BelongToBuilding";         /* 属性 */
        String before = "B1";                    /* 原来的属性值 */
        String after = "B1";                     /* 更新后的属性值 */
        OWL instance = new OWL(owlFilePath);
        
        boolean result = instance.updateObjectValueForInstance(iri, key, before, after);
        assertNotNull(result);
        System.out.println(String.valueOf(result));
    }
    
}
