// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.owl2query.parser;

import java.net.URI;

/**
 * <p>
 * Title: Vocabulary for nonOWL SPARQL-DL constructs.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */

public class SparqldlExtensionsVocabulary {

	public static final String sdlExtensionsBase = "http://pellet.owldl.com/ns/sdle#";

	public static final String sdlExtensionsNs = "sdle";

	// SPARQL-DL extensions
	public static final URI strictSubClassOf = URI.create(sdlExtensionsBase + "strictSubClassOf");

    public static final URI directSubClassOf = URI.create(sdlExtensionsBase + "directSubClassOf");

    public static final URI strictSubPropertyOf = URI.create(sdlExtensionsBase + "strictSubPropertyOf");

    public static final URI directSubPropertyOf = URI.create(sdlExtensionsBase + "directSubPropertyOf");

	public static final URI directType = URI.create(sdlExtensionsBase + "directType");
}
