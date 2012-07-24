/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.medical.application.device.simulator.gui.impl.component;

import java.util.HashMap;
import java.util.Set;

import nextapp.echo.app.Button;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Font;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.extras.app.DropDownMenu;
import nextapp.echo.extras.app.menu.DefaultMenuModel;
import nextapp.echo.extras.app.menu.DefaultMenuSelectionModel;
import nextapp.echo.extras.app.menu.DefaultOptionModel;

import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.medical.application.device.web.common.impl.component.FloatingButton;
import org.medical.application.device.web.common.util.BundleResourceImageReference;
import org.osgi.framework.Bundle;

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;
import fr.liglab.adele.icasa.environment.SimulationManager.Zone;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public class UserPane extends ContentPane {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 974309121082722498L;

	private ResourceImageReference USER_IMAGE = new ResourceImageReference("/User.png");

	private final SimulatorActionPane m_parent;

	private final TextField m_userName;

	private final Grid m_grid;

	private RoomPosition outsideRoomPosition;
	
	private final HashMap<String, UserEntry> m_users = new HashMap<String, UserEntry>();
	
	private final HashMap<String, RoomPosition> roomPositions = new HashMap<String, RoomPosition>();
	
	

	public UserPane(final SimulatorActionPane parent) {
		m_parent = parent;
				
		// Add the deafult room position
		addRoomPosition("--- Outside ---", -1, -1);
		outsideRoomPosition = roomPositions.get("--- Outside ---");
		
		//initializeRoomPositions();
		
		m_parent.getApplicationInstance();
		//USER_IMAGE =  new ResourceImageReference(m_parent.getApplicationInstance().getUserImage());
		Bundle bundle = BaseHouseApplication.getBundle();
		USER_IMAGE =  new BundleResourceImageReference(m_parent.getApplicationInstance().getUserImage(), bundle);
		
		// Create the image label.
		
		final Label image = new Label(
		      new BundleResourceImageReference(USER_IMAGE.getResource(), new Extent(50), new Extent(50), bundle));
		
		
		final GridLayoutData imageLayout = new GridLayoutData();
		imageLayout.setRowSpan(2);
		image.setLayoutData(imageLayout);
		// Create the user creation text field and button.
		m_userName = new TextField();
		final Button addUserButton = new Button("Add user");
		addUserButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = -5868083607668893407L;

			@Override
			public void actionPerformed(ActionEvent e) {
				addUser(m_userName.getText());
				m_userName.setText("");
			}
		});
		// Create the user table
		// Name || Location || RemoveButton
		m_grid = new Grid(3);
		final GridLayoutData gridLayout = new GridLayoutData();
		gridLayout.setColumnSpan(2);
		m_grid.setLayoutData(gridLayout);
		m_grid.setInsets(new Insets(3, 1));
		final Font headerFont = new Font(Font.HELVETICA, Font.BOLD, new Extent(12, Extent.PT));
		final Label name = new Label("Name");
		name.setFont(headerFont);
		final Label location = new Label("Location");
		location.setFont(headerFont);
		final Label remove = new Label("Remove");
		remove.setFont(headerFont);
		// Create the grid and add all components.
		final Grid grid = new Grid(2);
		grid.add(image);
		grid.add(m_userName);
		grid.add(addUserButton);
		grid.add(m_grid);
		add(grid);
	}
	
	public void initializeRoomPositions() {
		/*
		String homeType = m_parent.getApplicationInstance().getHomeType(); 
		
		if (homeType.equals("type1")) {
			addRoomPosition("livingroom", 220, 220);
			addRoomPosition("kitchen", 115, 450);
			addRoomPosition("bedroom", 568, 268);
			addRoomPosition("bathroom", 530, 450);
			addRoomPosition("Entrance", 325, 533);			
		} else if (homeType.equals("type2")) {
			addRoomPosition("livingroom", 500, 180);
			addRoomPosition("kitchen", 490, 405);
			addRoomPosition("bedroom", 130, 400);
			addRoomPosition("bathroom", 140, 108);
			addRoomPosition("Entrance", 325, 533);						
		} else {
			addRoomPosition("livingroom", 220, 220);
			addRoomPosition("kitchen", 115, 450);
			addRoomPosition("bedroom", 568, 268);
			addRoomPosition("bathroom", 530, 450);
			addRoomPosition("Entrance", 325, 533);
		}
		*/
		SimulationManager simulationManager = m_parent.getApplicationInstance().getSimulationManager();
		
		Set<String> envs = simulationManager.getEnvironments();
		for (String env : envs) {
	      Zone zone = simulationManager.getEnvironmentZone(env);
	      if (zone!=null) {
	      	// User position when (s)he is moved
	      	int x = ((zone.leftX + zone.rightX) / 2)-20;
	      	int y = ((zone.topY + zone.bottomY) / 2)-20;
	      	addRoomPosition(env, x, y);
	      }
      }
	   
   }

	public void addRoomPosition(String roomName, int x, int y) {		
		RoomPosition roomPosition =  new RoomPosition(roomName);
		roomPosition.x = x;
		roomPosition.y = y;
		roomPositions.put(roomName, roomPosition);
	}

	private synchronized void addUser(final String name) {
		final String normalizedName = name.trim();
		if (normalizedName.isEmpty()) {
			showErrorWindow("The name of the user is empty.");
			return;
		} else if (m_users.containsKey(normalizedName)) {
			showErrorWindow("The user \"" + normalizedName + "\"already exists.");
			return;
		}
		final UserEntry entry = new UserEntry();
		entry.name = normalizedName;
		entry.label = new Label(normalizedName);
		// Create the location selection menu.
		final DefaultMenuModel model = new DefaultMenuModel();

		Set<String> rooms = roomPositions.keySet();
		for (String room : rooms) {
			model.addItem(new DefaultOptionModel(room, room, null));
      }
		
		final DefaultMenuSelectionModel menuSelect = new DefaultMenuSelectionModel();
		entry.locationMenu = new DropDownMenu(model, menuSelect);
		menuSelect.setSelectedId(outsideRoomPosition.roomName);
		entry.roomPosition = outsideRoomPosition;
		entry.locationMenu.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1797893813768038434L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				m_parent.getApplicationInstance().getSimulationManager()
				      .setUserLocation(entry.name, e.getActionCommand().toLowerCase());
			}

		});
		// Create the remove button.
		entry.removeButton = new Button(new ResourceImageReference("/Remove.png"));
		entry.removeButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1797893813768038434L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// Remove the user.
				m_users.remove(normalizedName);
				m_grid.remove(entry.label);
				m_grid.remove(entry.locationMenu);
				m_grid.remove(entry.removeButton);
				if (entry.image != null) {
					m_parent.getApplicationInstance().getHousePane().remove(entry.image);
				}
			}
		});
		// Add the created components to the grid.
		m_grid.add(entry.label);
		m_grid.add(entry.locationMenu);
		m_grid.add(entry.removeButton);
		m_users.put(normalizedName, entry);
	}

	public synchronized void moveUser(final String userName, final Position position) {
		if (!m_users.containsKey(userName)) {
			addUser(userName);
		}
		final UserEntry entry = m_users.get(userName);
		final String envId = m_parent.getApplicationInstance().getSimulationManager()
		      .getEnvironmentFromPosition(position);
		RoomPosition roomPosition = null;
		if (envId != null) {
			try {
				roomPosition = roomPositions.get(envId);
				//newLocation = Location.valueOf(envId.toUpperCase());
			} catch (Exception e) {
				// Ouch !
				e.printStackTrace();
				roomPosition = outsideRoomPosition;
			}
		} else {
			roomPosition = outsideRoomPosition;
		}
		if (roomPosition!=null) {
			moveUserToLocation(entry, roomPosition);
			entry.locationMenu.getSelectionModel().setSelectedId(roomPosition.roomName);
			// TODO update selected item of entry menu!!!			
		}
	}

	private void moveUserToLocation(final UserEntry entry,
	      final RoomPosition roomPosition) {
		if (entry.roomPosition == roomPosition) {
			return;
		}
		if (entry.roomPosition != outsideRoomPosition) {
			// Remove the previous image.
			m_parent.getApplicationInstance().getHousePane().remove(entry.image);
		}
		if (roomPosition != outsideRoomPosition) {
			// Create the new image.
			entry.image = new FloatingButton(roomPosition.x, roomPosition.y, USER_IMAGE, entry.name);
			m_parent.getApplicationInstance().getHousePane().add(entry.image);
		}
		entry.roomPosition = roomPosition;
	}



	private void showErrorWindow(final String error) {
		final WindowPane window = new WindowPane();
		// Create the icon.
		final Label icon = new Label(new ResourceImageReference("/Error.png"));
		// Create the message label.
		final Label message = new Label(error);
		// Create the confirmation button.
		final Button okButton = new Button("OK");
		okButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = -5553078676984001972L;

			@Override
			public void actionPerformed(ActionEvent e) {
				window.userClose();
			}
		});
		final GridLayoutData okButtonLayout = new GridLayoutData();
		okButtonLayout.setColumnSpan(2);
		okButton.setLayoutData(okButtonLayout);
		// Create the grid.
		final Grid grid = new Grid(2);
		grid.add(icon);
		grid.add(message);
		grid.add(okButton);
		final ContentPane content = new ContentPane();
		content.add(grid);
		// Populate the window.
		window.setTitle("ERROR");
		window.setModal(true);
		window.setMaximizeEnabled(false);
		window.setClosable(false);
		window.add(content);
		m_parent.getApplicationInstance().getWindow().getContent().add(window);
	}
	
	private static class UserEntry {
		private String name;
		private Label label;
		private DropDownMenu locationMenu;
		private Button removeButton;
		private FloatingButton image;
		//private Location location;
		private RoomPosition roomPosition;
	}
	
	private class RoomPosition {
		private String roomName;
		public int x;
		public int y;
		
		RoomPosition(String roomName) {
			this.roomName = roomName;
			x = 0;
			y = 0;
		}		
	}

}
