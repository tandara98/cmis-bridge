How to setup with Portal 5
==========================

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

License
=======

Copyright 2013 Backbase B.V.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
