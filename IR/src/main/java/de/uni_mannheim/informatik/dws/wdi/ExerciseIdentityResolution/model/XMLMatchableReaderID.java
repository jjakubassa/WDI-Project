package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

public class XMLMatchableReaderID extends XMLMatchableReader<Matchable, Matchable> {

	protected String getValueFromElement(Node node, String childName) {

		// get all child nodes
		NodeList children = node.getChildNodes();
		
		// iterate over the child nodes until the node with childName is found
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);

			// check the node type and the name
			if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE
					&& child.getNodeName().equals(childName)) {

				return child.getTextContent().trim();

			}
		}

		return null;
	}

	@Override
	public Matchable createModelFromElement(Node node, String provenanceInfo) {
		return null;
	}
}


