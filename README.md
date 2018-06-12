# JAXB @XmlElementWrapper Plugin

[![Build Status](https://travis-ci.org/dmak/jaxb-xew-plugin.svg)](https://travis-ci.org/dmak/jaxb-xew-plugin)
[![Coverage Status](https://coveralls.io/repos/github/dmak/jaxb-xew-plugin/badge.svg?branch=master)](https://coveralls.io/github/dmak/jaxb-xew-plugin?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jaxb-xew-plugin/jaxb-xew-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jaxb-xew-plugin/jaxb-xew-plugin)
[![Javadoc](http://javadoc.io/badge/com.github.jaxb-xew-plugin/jaxb-xew-plugin.svg)](http://www.javadoc.io/doc/com.github.jaxb-xew-plugin/jaxb-xew-plugin)

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
			<include name="jaxb2-basics-tools-0.12.0.jar" />
			<include name="jaxb-xew-plugin-1.10.jar" />
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
						<version>1.10</version>
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
						<version>1.10</version>
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
			<version>1.10</version>
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

    xjc "com.github.jaxb-xew-plugin:jaxb-xew-plugin:1.10"
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

It could be that some plugins fail if executed after Xew, check [this my comment](https://github.com/dmak/jaxb-xew-plugin/issues/48#issuecomment-241999382) for further details.

### Episode file

For correct generation of episode file the corresponding XJC options should follow `-Xxew`, for example:

`... -Xxew -episode <file> ...`

This will trigger episode plugin _after_ Xew plugin and episode file will be correctly generated. Note that `maven-jaxb2-plugin` adds `-episode` to the end of argument list, hence works correctly.

### `equals`, `hashCode`, `simpleEquals`, `simpleHashCode`, `fluent-api`, `value-constructor`, `jaxbindex` and [`immutable`](https://github.com/sabomichal/immutable-xjc) plugins

These plugins should be activated _after_ Xew plugin:

`... -Xxew -Xequals -XhashCode -XsimpleEquals -XsimpleHashCode -Xfluent-api -Xvalue-constructor -Xjaxbindex -immutable ...`

Otherwise (if they are activated before) Xew plugin cannot revert/complement the changes they made and compile-time error is guaranteed.

### `setters` plugin

This plugin should be activated _before_ Xew plugin due to problem described in [issue#15](https://github.com/dmak/jaxb-xew-plugin/issues/15):

`... -Xsetters -Xxew ...`

## Contribution

If you have time and desire to contribute to this project you can do it in many ways:

* Improve this very documentation.
* Implement unit tests.
* Provide extra samples.

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
  - all unit tests run successfully
- Commit to your own fork, mentioning the ticket number in commit message (`Implemented nice feature (fixes #22)`). Check [here](https://github.com/blog/831-issues-2-0-the-next-generation) the commit message syntax sugar.
- [Request for pull](http://help.github.com/send-pull-requests/).

If you provide the code in any way (patch, pull request, post, comment, …) you automatically agree with a [project license](#license).

### Bug reports

This very open source project is maintained and improved during my free time. That is why I have no ability to test the functionality will all different combinations of Java, JAXB, JAXB plugins and Maven plugins. I leave this to community. Hence please in newly created bug report:
- mention Java version
- attach a simple minimalistic project (preferably based on Ant, Maven, Gradle) that demonstrates the problem (alternatively one can clone this repository plus alter one of the [`samples`](samples/))

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
* Use the following `settings.xml` for your Maven (see [Sharing Files with Build Agents](http://wiki.cloudbees.com/bin/view/DEV/Sharing+Files+with+Build+Executors) about how to share `settings.xml` with build nodes on CloudBees and [Configuring GPG/PGP for Maven Releases](https://nblair.github.io/2015/10/29/maven-gpg-sonatype/) about GPG profile):
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
				<gpg.useagent>false</gpg.useagent>
				<gpg.passphrase>...passphrase...</gpg.passphrase>
				<gpg.homedir>/private/...github_user.../gpg</gpg.homedir>
			</properties>
		</profile>
	</profiles>
</settings>
```
* Make sure you have git ≥ v1.7.10 installed, otherwise you may face [this bug#341221](https://bugs.eclipse.org/bugs/show_bug.cgi?id=341221).
* For JDK 1.6 you need to put [JAXB API](http://search.maven.org/#artifactdetails|javax.xml.bind|jaxb-api|2.2.3|jar) ≥ v2.2.3 to `jre/lib/endorsed` directory of JDK which is used to build the project. Otherwise build will fail with `java.lang.NoSuchMethodError: javax.xml.bind.annotation.XmlElementWrapper.required()Z`.
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

* [JAXB-784](https://github.com/javaee/jaxb-v2/issues/784) is about NPE in `JAnnotationUse#getAnnotationMembers()` method.
* [JAXB-884](https://github.com/javaee/jaxb-v2/issues/884) is about ClassCastException in `JAnnotationArrayMember#annotations()` method.
* [JAXB-878](https://github.com/javaee/jaxb-v2/issues/878) and [JAXB-879](https://github.com/javaee/jaxb-v2/issues/879) describe the lack of public getters for class fields.
* [JAXB-957](https://github.com/javaee/jaxb-v2/issues/957) mentions what need to be added to make it possible for the inner class to be moved to another class or package.
* [JAXB-883](https://github.com/javaee/jaxb-v2/issues/883) does not allow to learn if "simpleMode" setting is enabled, which in its turn controls plural form for collection property names. There are however some more difficulties to overcome.
* [JAXB-1107](https://github.com/javaee/jaxb-v2/issues/1107) – marshalling of text nodes for mixed-mode contents

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
