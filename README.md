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

First you need to download the plugin jar (for example, from [Maven repository](http://mirrors.ibiblio.org/pub/mirrors/maven2/com/github/jaxb-xew-plugin/jaxb-xew-plugin)) and put it to your project `lib` folder.

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

#### maven-jaxb2-plugin

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

#### jaxb2-maven-plugin

    <plugin>
    	<groupId>org.codehaus.mojo</groupId>
    	<artifactId>jaxb2-maven-plugin</artifactId>
    	<version>1.5</version>
    	<executions>
    		<execution>
    			<phase>generate-sources</phase>
    			<goals>
    				<goal>xjc</goal>
    			</goals>
    			<configuration>
    				<verbose>true</verbose>
    				<outputDirectory>${project.build.sourceDirectory}</outputDirectory>
    				<schemaDirectory>xsd</schemaDirectory>
    				<clearOutputDir>false</clearOutputDir>
    
    				<extension>true</extension>
    				<arguments>-no-header -Xxew -Xxew:instantiate lazy -Xxew:delete</arguments>
    			</configuration>
    		</execution>
    	</executions>
    	<dependencies>
    		<dependency>
    			<groupId>com.github.jaxb-xew-plugin</groupId>
    			<artifactId>jaxb-xew-plugin</artifactId>
    			<version>1.0</version>
    		</dependency>
    		<!-- 
    		 | We need to update the jaxb-xjc plugin version from 2.1.13 to the 2.2.4-1 version 
    		 | used by the jaxb-xew-plugin (version 2.1.13 which does not have the required 
    		 | method com.suun.codemodel.JAnnotatable.annotations()Ljava/util/Collection).
    		 -->
    		<dependency>
    			<groupId>com.sun.xml.bind</groupId>
    			<artifactId>jaxb-xjc</artifactId>
    			<version>2.2.4-1</version>
    		</dependency>
    	</dependencies>
    </plugin>

You can find more examples of this plugin in `samples` directory.

## Contribution

If you have time and desire to contribute to this project you can do it in many ways:

* Improve this very documentation.
* Implement Unit tests.
* Provide more samples.

### Development

Everybody is very welcomed to send patches by email. But the best way would be:

- Fork the repository
- Apply the formatting rules (the ones for Eclipse can be found in [`dist`](https://github.com/dmak/jaxb-xew-plugin/tree/master/dist) folder)
- Do the changes
- Commit to your own fork
- [Request for pull](http://help.github.com/send-pull-requests/)

#### Code style

* There are no specific coding and naming conventions for this project except ones given in [Code Conventions for the Java Programming Language](http://www.oracle.com/technetwork/java/codeconv-138413.html) by Sun. Use best practices and common sense.
* For [code formatting](https://github.com/dmak/jaxb-xew-plugin/tree/master/dist/eclipse-code-fomatting-rules.xml) basically Eclipse build-in formatting rules were used with following changes:
  - Indentation → Align fields on columns: on
  - Indentation → Tab policy: Mixed
  - Indentation → Use spaces to indent wrapped lines: on
  - Line Wrapping → Maximum line width: 120
  - Line Wrapping → Default indentation for wrapped lines: 3
  - Comments → Maximum line width for comments: 120
  - Comments → Enable line comment formatting: off
  - New Lines → Insert new line in empty anonymous class body: off
  - New Lines → Insert new line in empty block: off

#### Release procedure

* Read [Sonatype OSS Maven Repository Usage Guide](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide) from cover to cover.
* Use the following `settings.xml` for your Maven:

        <settings>
        	<!-- Optional proxy configuration (if applicable to your environment) -->
        	<proxies>
        		<proxy>
        			<active>true</active>
        			<protocol>http</protocol>
        			<host>proxy</host>
        			<port>8080</port>
        			<nonProxyHosts>*.internal.domain</nonProxyHosts>
        		</proxy>
        		<proxy>
        			<active>true</active>
        			<protocol>https</protocol>
        			<host>proxy</host>
        			<port>8080</port>
        			<nonProxyHosts>*.internal.domain</nonProxyHosts>
        		</proxy>
        	</proxies>
        
        	<servers>
        		<server>
        			<id>sonatype-nexus-snapshots</id>
        			<username>...sonatype_user...</username>
        			<password>...sonatype_password...</password>
        		</server>
        		<server>
        			<id>sonatype-nexus-staging</id>
        			<username>...sonatype_user...</username>
        			<password>...sonatype_password...</password>
        		</server>
        	</servers>
        
        	<profiles>
        		<profile>
        			<id>gpg</id>
        			<properties>
        				<gpg.passphrase>...passphrase...</gpg.passphrase>
        			</properties>
        		</profile>
        	</profiles>
        </settings>

* For Hudson freestyle job specify:
  * Pre-release step `git checkout master; git reset --hard origin/master` (see [Can't get automated release working with Hudson + Git + Maven Release Plugin](http://stackoverflow.com/questions/1877027) for more details about the problem).
  * Next step (release): `release:prepare release:perform -Pstage-release -Pgpg -Dresume=false -Dusername=...github_user... -Dpassword=...github_password...`

### Algorithm description

The plugin flow consists of the following parts: 

* Parse arguments.
* Find classes which are candidates for removal:
  1. The candidate class must have exactly one property
  2. The candidate class should not extend any other class (as the total number of properties will be more than 1)
* Visit all classes again to check if the candidate is not eligible for removal:
  1. If there are classes that extend the candidate
  2. If there are class fields, that refer the candidate by e.g. `@XmlElementRef` annotation
* Visit all classes again to replace the property having the candidate class type with collection plus `@XmlElementWrapper` annotation. On this step getters/setters are update and ObjectFactory methods are corrected. Also lazy initialization policy is applied.
* Candidates which are still marked for removal are finally removed (and ObjectFactory is updated accordingly).

## Authors

Original code by [Bjarne Hansen](http://www.conspicio.dk/blog/bjarne/jaxb-xmlelementwrapper-plugin). Committers:

* [Dmitry Katsubo](http://www.linkedin.com/in/dkatsubo)
* [David Matheson](https://github.com/davidfmatheson)

## License

The whole project is licensed under [LGPLv3](http://www.gnu.org/licenses/lgpl-3.0.html) (or any later version).
