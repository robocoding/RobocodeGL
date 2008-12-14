/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.repository;


import robocode.control.RobotSpecification;


/**
 * @author Pavel Savara (original)
 */
public interface IHiddenSpecificationHelper {

	RobotSpecification createSpecification(FileSpecification fileSpecification);
	FileSpecification getFileSpecification(RobotSpecification specification);
	void setValid(RobotSpecification specification, boolean value);
	void setTeamName(RobotSpecification specification, String teamName);
	String getTeamName(RobotSpecification specification);
}