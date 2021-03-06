/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger;

import java.nio.file.Path;

/**
 * Stores the location of the Swagger file.
 *
 * @author Sven Bayer
 */
public final class SwaggerFileFolder {

	private static SwaggerFileFolder swaggerFileFolder;

	private Path pathToSwaggerFile;

	private SwaggerFileFolder() {
	}

	public static SwaggerFileFolder instance() {
		if (swaggerFileFolder == null) {
			swaggerFileFolder = new SwaggerFileFolder();
		}
		return swaggerFileFolder;
	}

	public Path getPathToSwaggerFile() {
		return this.pathToSwaggerFile;
	}

	public void setPathToSwaggerFile(Path pathToSwaggerFile) {
		this.pathToSwaggerFile = pathToSwaggerFile;
	}

}
