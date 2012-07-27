How to setup with Portal 5
==============
1. Add cmis bridge dependency and mashup to POM

	<backbase.portal.mashup.version>5.0.0</backbase.portal.mashup.version>

	<dependency>
		<groupId>com.backbase.expert</groupId>
		<artifactId>cmis-bridge</artifactId>
		<version>${backbase.expert.cmisbridge.version}</version>
	</dependency>
	
	<dependency>
		<groupId>com.backbase.services.mashup</groupId>
		<artifactId>mashup-embedded</artifactId>
		<version>${backbase.portal.mashup.version}</version>
	</dependency>

2. Add cmis.properties file to /src/main/resources

3. Setup PTC 

4. Setup widget