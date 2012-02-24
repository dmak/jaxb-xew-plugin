# JAXB @XmlElementWrapper Plugin

## Description

This JAXB plugin utilizes the power of `@XmlElementWrapper` annotation. Originally `xjc` trends to create wrapper classes which are the containers for collections. This plugin goes through all properties to find ones which can be represented in the model in more optimal way.

## The problem origin in details

To illustrate the problem let's take the following XSD:

    <?xml version="1.0" encoding="UTF-8"?>
    <xs:schema
    	xmlns:xs="http://www.w3.org/2001/XMLSchema"
    	xmlns:xml="http://www.w3.org/XML/1998/namespace"
    	elementFormDefault="qualified">
    
    	<xs:element name="order">
    		<xs:complexType>
    			<xs:sequence>
    				<xs:element ref="items" />
    			</xs:sequence>
    		</xs:complexType>
    	</xs:element>
    
    	<xs:element name="items">
    		<xs:complexType>
    			<xs:sequence>
    				<xs:element name="item" type="xs:string" maxOccurs="unbounded" />
    			</xs:sequence>
    		</xs:complexType>
    	</xs:element>
    </xs:schema>

From this XSD by default `xjc` will generate two classes:

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "items" })
    @XmlRootElement(name = "order")
    public class Order {
    
    	@XmlElement(required = true)
    	protected Items	items;
    
    	public Items getItems() {
    		return items;
    	}
    
    	public void setItems(Items value) {
    		this.items = value;
    	}
    }

and

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "item" })
    @XmlRootElement(name = "items")
    public class Items {
    
    	@XmlElement(required = true)
    	protected List<String>	item;
    
    	public List<String> getItem() {
    		if (item == null) {
    			item = new ArrayList<String>();
    		}
    		return this.item;
    	}
    }

So to access a particular item one need to write a but clumsy code `order.getItems().getItems().get(itemIndex)`. The solution is to use `@XmlElementWrapper` which cures exactly this case. The result will be only one class with direct access to items:

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = { "items" })
    @XmlRootElement(name = "order")
    public class Order {
    
    	@XmlElementWrapper(name = "items", required = true)
    	@XmlElement(name = "item")
    	protected List<String>	items;
    
    	public List<String> getItems() {
    		if (items == null) {
    			items = new ArrayList<String>();
    		}
    		return items;
    	}
    
    	public void setItems(List<String> items) {
    		this.items = items;
    	}
    }

## Usage

The plugin is used together with the `xjc` from the command line or from an Ant task or via `maven-jaxb2-plugin`.

The following options are applicable for plugin:

<table>
<tr>
	<th>Option</th>
	<th>Comment</th>
</tr>
<tr>
	<td>-Xxew</td>
	<td>Activate the XML Element Wrapper plugin</td>
</tr>
<tr>
	<td>-Xxew:include filename</td>
	<td>Specify a filename with candidate classes to include in the compilation.</td>
</tr>
<tr>
	<td>-Xxew:exclude filename</td>
	<td>Specify a filename with candidate classes to exclude from the compilation.</td>
</tr>
<tr>
	<td>-Xxew:summary filename</td>
	<td>Specify a filename to contain summary information on the compilation.</td>
</tr>
<tr>
	<td>-Xxew:collection FQCN</td>
	<td>Specify the class name of the collection type to use.</td>
</tr>
<tr>
	<td>-Xxew:instantiate [lazy|early]</td>
	<td>Specify when the collection class should be instantiated.</td>
</tr>
<tr>
	<td>-Xxew:delete</td>
	<td>Delete candidate classes having been replaced during compilation.</td>
</tr>
</table>

### Ant task

To use the plugin from Ant you will need something like the following in your build file:

    <taskdef name="xjc" classname="com.sun.tools.xjc.XJCTask">
    	<classpath>
    		<fileset dir="${lib}/jaxb" includes="*.jar" />
    		<fileset dir="lib" includes="jaxb-xew-plugin.jar" />
    	</classpath>
    </taskdef>
    
    <xjc destdir="${src-generated}" package="dk.conspicio.example.xml2code.v2">
    	<arg value="-Xxew" />
    	<arg value="-Xxew:summary ${build}/xew-summary.txt" />
    	<arg value="-Xxew:instantiate lazy" />
    	<schema dir="xsd" includes="*.xsd" />
    	<binding dir="xsd" includes="*.xjb" />
    </xjc>

### Maven

    <plugin>
    	<groupId>org.jvnet.jaxb2.maven2</groupId>
    	<artifactId>maven-jaxb2-plugin</artifactId>
    	<version>0.8.1</version>
    	<executions>
    		<execution>
    			<phase>generate-sources</phase>
    			<goals>
    				<goal>generate</goal>
    			</goals>
    			<configuration>
    				<verbose>true</verbose>
    				<generateDirectory>${project.build.sourceDirectory}</generateDirectory>
    				<schemaDirectory>xsd</schemaDirectory>
    				<removeOldOutput>false</removeOldOutput>
    				<episode>false</episode>
    
    				<extension>true</extension>
    				<args>
    					<arg>-no-header</arg>
    					<arg>-Xxew</arg>
    					<arg>-Xxew:instantiate lazy</arg>
    					<arg>-Xxew:delete</arg>
    				</args>
    				<plugins>
    					<plugin>
    						<groupId>com.github.jaxb-xew-plugin</groupId>
    						<artifactId>jaxb-xew-plugin</artifactId>
    						<version>1.0</version>
    					</plugin>
    				</plugins>
    			</configuration>
    		</execution>
    	</executions>
    </plugin>

You can find more examples of this plugin in `samples` directory.
