/*
 * #%L
 * OW2 Chameleon - Fuchsia Framework
 * %%
 * Copyright (C) 2009 - 2015 OW2 Chameleon
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.openhab.binding.zwave.internal.protocol.initialization;

/**
 * Copyright (c) 2010-2015, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.openhab.binding.zwave.internal.protocol.ZWaveNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZWaveNodeSerializer class.
 * 
 * Serializes nodes to XML and back again.
 * 
 * This is an in-memory serializer used temporarily in this importer to override the serializer
 * provided by openHAB, and avoid dependencies in external xml libraries.
 * 
 * TODO evaluate the minimal set of libraries required to be able to use the full serialization in
 * OpenHab
 * 
 */
public class ZWaveNodeSerializer {

	private static final Logger logger = LoggerFactory.getLogger(ZWaveNodeSerializer.class);

	private static final Map<Integer,ZWaveNode> nodes = new ConcurrentHashMap<Integer, ZWaveNode>();
	
	/**
	 * Constructor. Creates a new instance of the {@link ZWaveNodeSerializer}
	 * class.
	 */
	public ZWaveNodeSerializer() {
		logger.trace("Initializing fake ZWaveNodeSerializer.");
	}

	/**
	 * Serializes an XML tree of a {@link ZWaveNode}
	 * 
	 * @param node
	 *            the node to serialize
	 */
	public void SerializeNode(ZWaveNode node) {
		logger.trace("serializing fake ZWaveNodeSerializer.");
		nodes.put(node.getNodeId(), node);
	}

	/**
	 * Deserializes an XML tree of a {@link ZWaveNode}
	 * 
	 * @param nodeId
	 *            the number of the node to deserialize
	 * @return returns the Node or null in case Serialization failed.
	 */
	public ZWaveNode DeserializeNode(int nodeId) {
		logger.trace("deserializing fake ZWaveNodeSerializer.");
		return nodes.get(nodeId);
	}
	
	/**
	 * Deletes the persistence store for the specified node.
	 * 
	 * @param nodeId The node ID to remove
	 * @return true if the file was deleted
	 */
	public boolean DeleteNode(int nodeId) {
		logger.trace("deleting node fake ZWaveNodeSerializer.");
		return nodes.remove(nodeId) != null;
	}
}
