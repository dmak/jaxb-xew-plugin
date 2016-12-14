# JAXB @XmlElementWrapper Plugin

## Description

This JAXB plugin utilises the power of `@XmlElementWrapper` annotation. Originally `xjc` trends to create wrapper classes which are the containers for collections. This plugin goes through all properties to find ones which can be represented in the model in more optimal way.

If you like this plugin, please give in a star in GutHub! Report the issues to [bugtracker](https://github.com/dmak/jaxb-xew-plugin/issues/) or use [`jaxb-xew-plugin`](http://stackoverflow.com/questions/tagged/jaxb-xew-plugin) tag on StackOverflow.

## The problem origin in details

To illustrate the problem let's take the following XSD:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:xml="http://www.w3.org/XML/1998/namespace"
	elementFormDefault="qualified">

	<xsd:element name="order">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element ref="items" />
			</xsd:sequence>
		</xsd:complexType>
	</xsd:element>

	<xsd:element name="items">
		<xsd:complexType>
			<xsd:sequence>
				<xsd:element name="item" type="xsd:string" maxOccurs="unbounded" />
			</xsd:sequence>
		</xsd:complexType>
	</xsd:element>
</xsd:schema>
```
From this XSD by default `xjc` will generate two classes:
```java
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
```
and
```java
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
```
So to access a particular item one need to write a but clumsy code `order.getItems().getItems().get(itemIndex)`. The solution is to use `@XmlElementWrapper` which cures exactly this case. The result will be only one class with direct access to items:
```java
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
```
After this transformation of `Order` class, the class `Items` is not needed anymore and can be removed from the model. 

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
	<td>-Xxew:control filename</td>
	<td>Specify a file with classes to exclude/include/keep in the compilation.</td>
</tr>
<tr>
	<td>-Xxew:summary filename</td>
	<td>Specify a filename to contain summary information for the compilation.</td>
</tr>
<tr>
	<td>-Xxew:collection FQCN</td>
	<td>Specify the class name of the collection instance to use.</td>
</tr>
<tr>
	<td>-Xxew:collectionInterface FQCN</td>
	<td>Specify the class name of the collection interface to use.</td>
</tr>
<tr>
	<td>-Xxew:instantiate [early|lazy|none]</td>
	<td>Specify when the collection class should be instantiated: when class is created / when property is accessed from getter / not instantiated at all.</td>
</tr>
<tr>
	<td>-Xxew:plural</td>
	<td>Apply plural form to collection property (e.g. turn "item" into "items").</td>
</tr>
</table>

### Control file

This file allows to control how substituted classes (candidates) are handled. Each control file line specifies a fully qualified candidate class name or regex pattern (provided between slashes `/.../`) plus mode associated with it.

The following control modes are available:

<table>
<tr>
	<td>exclude</td>
	<td>Given candidate class is excluded from becoming candidate for substitution and will not be removed from the model.</td>
</tr>
<tr>
	<td>include</td>
	<td>Given candidate class is not excluded from becoming candidate for substitution. Used usually in conjunction with exclude to include back a part of the exclusion space.</td>
</tr>
<tr>
	<td>keep</td>
	<td>Given candidate class is not removed (kept) from model. Substitutions are nevertheless made.</td>
</tr>
</table>

The rules are attempted for match from top to bottom, so the last matched rule prevails.

Example of control file:
```
# RegEx to exclude all classes in given package and subpackages:
/org\.company\..*/=exclude

# Specific class marked for exclusion is now included back:
org.company.Processor=include
```
Empty lines or lines started with `#` (comment) are ignored. See also [`inner-element-control.txt`](src/test/resources/com/sun/tools/xjc/addon/xew/inner-element-control.txt) and other control files in test suit.

### JAXB customization

With JAXB customization it is possible to pass the same options as via XJC arguments. Here is the example with all global options enumerated:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema
	jaxb:version="2.0"
	xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
	xmlns:xew="http://github.com/jaxb-xew-plugin"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	jaxb:extensionBindingPrefixes="xew"
	elementFormDefault="qualified"
>
	<xsd:annotation>
		<xsd:appinfo>
			<xew:xew
				control="control.txt" summary="summary.txt"
				collection="java.util.LinkedHashSet" collectionInterface="java.util.Collection"
				instantiate="early" plural="true" />
		</xsd:appinfo>
	</xsd:annotation>
	
	...
</xsd:schema>
```
XJC arguments are overridden with JAXB global customizations, which are overridden with JAXB type customizations, which are overridden with JAXB field customizations. Per-type and per-field customizations cannot override `control` and `summary` configuration options, but have additionally `annotate` option, which if set to `false`, disables annotation/replacement of the particular field or all fields of the type/class (see [`element-with-customization.xsd`](src/test/resources/com/sun/tools/xjc/addon/xew/element-with-customization.xsd) example for global, per-type and per-field customization). Note that in contrast to `exclude` mode in control file, this option controls the behaviour for the particular field (or all fields of a type/class) which are being annotated with `@XmlElementWrapper`, but not the types/classes which are used for substitution (candidate classes). Example of per-field JXB file:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jaxb:bindings
	xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:xew="http://github.com/jaxb-xew-plugin"
	jaxb:extensionBindingPrefixes="xew"
	xmlns:xsi="http://www.w3.org/2000/10/XMLSchema-instance" version="2.1">

	<jaxb:bindings node="xsd:complexType[@name='container']">
		<jaxb:bindings node=".//xsd:element[@name='items']">
			<xew:xew annotate="false" />
		</jaxb:bindings>
	</jaxb:bindings>
</jaxb:bindings>
```

### Ant task

First you need to download the plugin jar (for example, from [Maven repository](http://mirrors.ibiblio.org/pub/mirrors/maven2/com/github/jaxb-xew-plugin/jaxb-xew-plugin)) and put it to your project `libs` folder together with other dependencies.

To use the plugin from Ant you will need something like the following in your build file:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="SunlightDataService" default="build" basedir=".">
	<path id="runtime.classpath">
		<fileset dir="libs">
			<include name="jaxb-xjc-2.2.11.jar" />
			<include name="jaxb-api-2.2.11.jar" />
			<include name="jaxb-core-2.2.11.jar" />
			<include name="commons-logging-1.1.1.jar" />
			<include name="commons-lang-2.2.jar" />
		</fileset>
	</path>
	<path id="xjc.classpath">
		<fileset dir="libs">
			<include name="jaxb2-basics-tools-0.6.5.jar" />
			<include name="jaxb-xew-plugin-1.8.jar" />
		</fileset>
	</path>
	<taskdef name="xjc" classname="com.sun.tools.xjc.XJCTask">
		<classpath>
			<path refid="runtime.classpath" />
		</classpath>
	</taskdef>
	<target name="build">
		<mkdir dir="target" />
		<xjc destdir="target" schema="xsd/test.xsd" package="org.mycompany" extension="true" removeOldOutput="true">
			<classpath refid="xjc.classpath" />
			<arg value="-Xxew" />
		</xjc>
	</target>
</project>
```

### Maven

#### maven-jaxb2-plugin

Note: `maven-jaxb2-plugin` prior to v0.8.0 was compiled against JAXB XJC API which _is not compatible with this plugin_. Versions [0.8.1…0.12.1] should work fine.
```xml
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
				</args>
				<plugins>
					<plugin>
						<groupId>com.github.jaxb-xew-plugin</groupId>
						<artifactId>jaxb-xew-plugin</artifactId>
						<version>1.8</version>
					</plugin>
				</plugins>
			</configuration>
		</execution>
	</executions>
</plugin>
```
Versions ≥ 0.12.2 work with `jaxb-xew-plugin` ≥ v1.8 (see [issue#50](https://github.com/dmak/jaxb-xew-plugin/issues/50) for other options):
```xml
<plugin>
	<groupId>org.jvnet.jaxb2.maven2</groupId>
	<artifactId>maven-jaxb2-plugin</artifactId>
	<version>0.12.2</version>
	<executions>
		<execution>
			<phase>generate-sources</phase>
			<goals>
				<goal>generate</goal>
			</goals>
			<configuration>
				<generateDirectory>${project.build.sourceDirectory}</generateDirectory>
				<extension>true</extension>
				<args>
					<arg>-Xxew</arg>
				</args>
				<plugins>
					<plugin>
						<groupId>com.github.jaxb-xew-plugin</groupId>
						<artifactId>jaxb-xew-plugin</artifactId>
						<version>1.8</version>
					</plugin>
				</plugins>
			</configuration>
		</execution>
	</executions>
</plugin>
```

#### jaxb2-maven-plugin

Note: `jaxb2-maven-plugin` ≤ v1.5 was compiled against JAXB XJC API v2.1.13 which _is not compatible with this plugin_, thus additional dependency is needed to be added to **plugin classpath**.
```xml
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
				<arguments>-no-header -Xxew -Xxew:instantiate lazy</arguments>
			</configuration>
		</execution>
	</executions>
	<dependencies>
		<dependency>
			<groupId>com.github.jaxb-xew-plugin</groupId>
			<artifactId>jaxb-xew-plugin</artifactId>
			<version>1.8</version>
		</dependency>
		<!--
		 | We need to update the jaxb-xjc dependency from v2.1.13 to v2.2.11
		 | used by the jaxb-xew-plugin (v2.1.13 does not have the required
		 | method com.sun.codemodel.JAnnotatable.annotations()Ljava/util/Collection).
		 -->
		<dependency>
			<groupId>com.sun.xml.bind</groupId>
			<artifactId>jaxb-xjc</artifactId>
			<version>2.2.11</version>
		</dependency>
	</dependencies>
</plugin>
```
`jaxb2-maven-plugin` ≥ v2.1 should work just fine.

You can find more examples of this plugin in [`samples`](samples/) directory (including how to call this plugin using `jaxws-maven-plugin` or `cxf-codegen-plugin`).

### Gradle
```
plugins {
    id 'java'
    id 'maven'
}

sourceCompatibility = 1.6
targetCompatibility = 1.6

repositories {
    mavenCentral()
}

project.ext {
    jaxbVersion = "2.2.11"
    generatedSourcesDir = "target/generated-sources"
}

sourceSets {
    generated {
        output.classesDir = "target/classes"
        java {
            srcDir project.generatedSourcesDir
        }
    }
    test {
        compileClasspath += generated.output
        runtimeClasspath += generated.output
    }
}

configurations {
    xjc
}

dependencies {
    compile "com.sun.xml.bind:jaxb-xjc:${jaxbVersion}"
    compile "com.sun.xml.bind:jaxb-core:${jaxbVersion}"
    compile "com.sun.xml.bind:jaxb-impl:${jaxbVersion}"
    compile "javax.xml.bind:jaxb-api:${jaxbVersion}"

    xjc "com.github.jaxb-xew-plugin:jaxb-xew-plugin:1.8"
    xjc "net.java.dev.jaxb2-commons:jaxb-fluent-api:2.1.8"
}

task processXSD {
    ant.taskdef(name: "xjc", classname: "com.sun.tools.xjc.XJCTask", classpath: configurations.compile.asPath)

    mkdir project.generatedSourcesDir
    ant.xjc(destdir: project.generatedSourcesDir, package: "com.mycompany", extension: true) {
        classpath {
            pathelement(path: configurations.xjc.asPath)
        }
        schema(dir: "xsd", includes: "test.xsd")
        arg(value: "-Xxew")
        arg(value: "-Xfluent-api")
    }
}

compileJava.dependsOn processXSD
```

## Compatibility and side effects

### Episode file

For correct generation of episode file the corresponding XJC options should follow `-Xxew`, for example:

`... -Xxew -episode <file> ...`

This will trigger episode plugin _after_ Xew plugin and episode file will be correctly generated. Note that `maven-jaxb2-plugin` adds `-episode` to the end of argument list, hence works correctly.

### `equals`, `hashCode`, `fluent-api`, `value-constructor` and `jaxbindex` plugins

These plugins should be activated _after_ Xew plugin:

`... -Xxew -Xequals -XhashCode -Xfluent-api -Xvalue-constructor -Xjaxbindex ...`

Otherwise (if they are activated before) Xew plugin cannot revert/complement the changes they made and compile-time error is guaranteed.

### `setters` plugin

This plugin should be activated _before_ Xew plugin due to problem described in [issue#15](https://github.com/dmak/jaxb-xew-plugin/issues/15):

`... -Xsetters -Xxew ...`

### `simpleEquals` and `simpleHashCode` plugins

These plugins don't work with `xew` as last one is causing side effects (see [#48](https://github.com/dmak/jaxb-xew-plugin/issues/48)).

## What's new

### v1.9 (future release)

* Bugs fixed ([#52](https://github.com/dmak/jaxb-xew-plugin/issues/52)).

### [v1.8](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.8|jar)

* Bugs fixed ([#48](https://github.com/dmak/jaxb-xew-plugin/issues/48)).
* Improvements:
  * added `exclude` customization option

### [v1.7](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.7|jar)

* Bugs fixed ([#45](https://github.com/dmak/jaxb-xew-plugin/issues/45)).

### [v1.6](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.6|jar)

* Bugs fixed ([#41](https://github.com/dmak/jaxb-xew-plugin/issues/41)).

### [v1.5](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.5|jar)

* Bugs fixed ([#32](https://github.com/dmak/jaxb-xew-plugin/issues/32), [#39](https://github.com/dmak/jaxb-xew-plugin/issues/39), [#40](https://github.com/dmak/jaxb-xew-plugin/issues/40)).

### [v1.4](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.4|jar)

* Bugs fixed ([#21](https://github.com/dmak/jaxb-xew-plugin/issues/21), [#32](https://github.com/dmak/jaxb-xew-plugin/issues/32), [#33](https://github.com/dmak/jaxb-xew-plugin/issues/33)).

### [v1.3](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.3|jar)

* Improvements:
  * More flexible control file ([#23](https://github.com/dmak/jaxb-xew-plugin/issues/23))
  * In-schema plugin customization ([#28](https://github.com/dmak/jaxb-xew-plugin/issues/28))
* Bugs fixed ([#22](https://github.com/dmak/jaxb-xew-plugin/issues/22), [#26](https://github.com/dmak/jaxb-xew-plugin/issues/26)).
* The option `-Xxew:delete` is removed as in majority of usecases it is set to true. Now by default plugin deletes all candidates. To prevent them from being deleted, create [control file](#control-file) with only line `/.*/=keep`.

### [v1.2](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.2|jar)

* Plugin is improved ([#14](https://github.com/dmak/jaxb-xew-plugin/issues/14)). Bugs fixed ([#12](https://github.com/dmak/jaxb-xew-plugin/issues/12), [#19](https://github.com/dmak/jaxb-xew-plugin/issues/19)).
* Plugin can now automatically apply plural form to collection properties (see `-Xxew:plural`).
* Unit tests now compile the model with `javac` and create JAXB context. Some test involve also XML marshalling/unmarshalling/comparing.

### [v1.1](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.1|jar)

* Plugin is re-worked. Bugs fixed ([#1](https://github.com/dmak/jaxb-xew-plugin/issues/1), [#3](https://github.com/dmak/jaxb-xew-plugin/issues/3), [#6](https://github.com/dmak/jaxb-xew-plugin/issues/6), [#7](https://github.com/dmak/jaxb-xew-plugin/issues/7)). Some functionality is possible only by accessing private fields, so Xew plugin may not work in security-managed environment.
* Testing framework introduced. XSDs in [`com/sun/tools/xjc/addon/xew/`](src/test/resources/com/sun/tools/xjc/addon/xew/) directory can be referred as collection of examples.
* Logging is done via `commons-logging`. Log level is configurable like this `mvn -Dorg.apache.commons.logging.simplelog.defaultlog=DEBUG`.

### [v1.0](http://search.maven.org/#artifactdetails|com.github.jaxb-xew-plugin|jaxb-xew-plugin|1.0|jar)

The original code of Bjarne Hansen, with some fixes.

## Contribution

If you have time and desire to contribute to this project you can do it in many ways:

* Improve this very documentation.
* Implement Unit tests.
* Provide more samples.

### Development

Everybody is very welcomed to send patches by email. But the best way would be:

- Fork the repository.
- Apply the [formatting rules](#code-style) (the ones for Eclipse can be found in [`dist`](dist) folder).
- Create a ticket in [bugtracker](https://github.com/dmak/jaxb-xew-plugin/issues). If applicable attach XSD that demonstrates the problem to the issue.
- Create a branch referring the ticket number (`git branch issue-22`).
- Do the changes.
- Verify your outgoing changeset. Make sure that:
  - your changeset is _minimal and sufficient_ for the feature implementation
  - your formatting rules have not caused changes in each and every line (e.g. due to end-of-line markers)
- Commit to your own fork, mentioning the ticket number in commit message (`Implemented nice feature (fixes #22)`). Check [here](https://github.com/blog/831-issues-2-0-the-next-generation) the commit message syntax sugar.
- [Request for pull](http://help.github.com/send-pull-requests/).

If you provide the code in any way (patch, pull request, post, comment, …) you automatically agree with a [project license](#license).

#### Code style

* There are no specific coding and naming conventions for this project except ones given in [Code Conventions for the Java Programming Language](http://www.oracle.com/technetwork/java/codeconv-138413.html) by Sun. Use best practices and common sense.
* For [code formatting](dist/eclipse-code-formatting-rules.xml) basically Eclipse build-in formatting rules were used with following changes:
  - Indentation → Align fields on columns: on
  - Indentation → Tab policy: Mixed
  - Indentation → Use spaces to indent wrapped lines: on
  - Line Wrapping → Maximum line width: 120
  - Line Wrapping → Default indentation for wrapped lines: 3
  - Comments → Maximum line width for comments: 120
  - Comments → Enable line comment formatting: off
  - New Lines → Insert new line in empty anonymous class body: off
  - New Lines → Insert new line in empty block: off
* TAB is used for alignment for XML/XSD/... files.

#### Build and release procedure

* Read [Sonatype OSS Maven Repository Usage Guide](http://central.sonatype.org/pages/ossrh-guide.html) from cover to cover.
* Use the following `settings.xml` for your Maven (see [Sharing Files with Build Agents](http://wiki.cloudbees.com/bin/view/DEV/Sharing+Files+with+Build+Executors) about how to share `settings.xml` with build nodes on CloudBees):
```xml
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
		<server>
			<id>github.com</id>
			<username>...github_user...</username>
			<password>...github_password...</password>
		</server>
	</servers>

	<profiles>
		<profile>
			<id>secure</id>
			<properties>
				<gpg.passphrase>...passphrase...</gpg.passphrase>
				<gpg.homedir>/private/...github_user.../gpg</gpg.homedir>
			</properties>
		</profile>
	</profiles>
</settings>
```
* Make sure you have git ≥ v1.7.10 installed, otherwise you may face [this bug#341221](https://bugs.eclipse.org/bugs/show_bug.cgi?id=341221).
* In case JDK  you need to put JAXB API ≥ v2.2.3 to `jre/lib/endorsed` directory of JDK which is used to build the project. Otherwise build will fail with `java.lang.NoSuchMethodError: javax.xml.bind.annotation.XmlElementWrapper.required()Z`.
* For Hudson freestyle job specify:
  * Pre-release step `git checkout master; git reset --hard origin/master` (see [Can't get automated release working with Hudson + Git + Maven Release Plugin](http://stackoverflow.com/questions/1877027) for more details about the problem).
  * Next step (release):
    `release:prepare release:perform -Pstage-release,secure -Dresume=false`

### Algorithm description

The plugin flow consists of the following parts:

* Parse arguments.
* Find classes which are candidates for removal:
  1. The candidate class should not extend any other class (as the total number of properties will be more than 1)
  2. The candidate class should have exactly one non-static property.
  3. This property should be a collection.
  4. This collection should have exactly one parametrisation type.
* Visit all classes again to check if the candidate is not eligible for removal:
  1. If there are classes that extend the candidate
  2. If there are class fields that refer the candidate by e.g. `@XmlElementRef` annotation
* Visit all classes again to replace the property having the candidate class type with collection plus `@XmlElementWrapper` annotation. On this step getters/setters are update and `ObjectFactory` methods are corrected. Also lazy initialization policy is applied.
* Candidates which are still marked for removal are finally removed (and `ObjectFactory` is updated accordingly).

There are many pitfalls in JAXB Code Model API which are forcing the developer to use dirty tricks (like accessing private fields) in order to implement the manipulation of code model. Among others:

* [JAXB-784](http://java.net/jira/browse/JAXB-784) is about NPE in `JAnnotationUse#getAnnotationMembers()` method.
* [JAXB-884](https://java.net/jira/browse/JAXB-884) is about ClassCastException in `JAnnotationArrayMember#annotations()` method.
* [JAXB-878](https://java.net/jira/browse/JAXB-878) and [JAXB-879](https://java.net/jira/browse/JAXB-879) describe the lack of public getters for class fields.
* [JAXB-957](https://java.net/jira/browse/JAXB-957) mentions what need to be added to make it possible for the inner class to be moved to another class or package.
* [JAXB-883](http://java.net/jira/browse/JAXB-883) does not allow to learn if "simpleMode" setting is enabled, which in its turn controls plural form for collection property names. There are however some more difficulties to overcome.
* [JAXB-1107](https://java.net/jira/browse/JAXB-1107) – marshalling of text nodes for mixed-mode contents

## Authors

Original code by [Bjarne Hansen](http://www.conspicio.dk/blog/bjarne/jaxb-xmlelementwrapper-plugin). Many thanks to committers:

* [Dmitry Katsubo](http://www.linkedin.com/in/dkatsubo)
* [Tobias Warneke](https://github.com/wumpz/)
* [David Matheson](https://github.com/davidfmatheson/)
* [Sebastian Steiner](https://github.com/sebisteiner/)
* [Colin Fairless](https://github.com/colin-yell/)
* [Patrick Crocker](https://github.com/patrickcrocker/)
* [Kermit The Frog](https://github.com/kermit-the-frog/)
* and others...

## License

The whole project is licensed under [LGPLv3](http://www.gnu.org/licenses/lgpl-3.0.html) (or any later version).
