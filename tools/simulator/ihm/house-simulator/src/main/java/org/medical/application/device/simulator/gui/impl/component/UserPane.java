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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import nextapp.echo.app.Button;
import nextapp.echo.app.Color;
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
import org.medical.application.device.web.common.impl.component.FloatingButtonDragSource;
import org.medical.application.device.web.common.impl.component.HousePane;
import org.medical.application.device.web.common.impl.component.event.DropEvent;
import org.medical.application.device.web.common.impl.component.event.DropListener;
import org.medical.application.device.web.common.util.BundleResourceImageReference;
import org.osgi.framework.Bundle;

import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;

/**
 * The simulator user pane
 * 
 * @author Gabriel Pedraza Ferreira
 */
public class UserPane extends ContentPane {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = 974309121082722498L;

	private ResourceImageReference USER_IMAGE = new BundleResourceImageReference("/User.png",
	      BaseHouseApplication.getBundle());

	private final SimulatorActionPane m_parent;

	private final TextField m_userName;

	private final Grid m_grid;

	private final HashMap<String, UserEntry> m_users = new HashMap<String, UserEntry>();
	
	private final List<String> rooms = new ArrayList<String>();
		
	private static String OUT_SIDE = "--- Outside ---";
	
	public UserPane(final SimulatorActionPane parent) {
		m_parent = parent;

		// Add the deafult room position

		rooms.add(OUT_SIDE);

		m_parent.getApplicationInstance();
		// USER_IMAGE = new
		Bundle bundle = BaseHouseApplication.getBundle();
		USER_IMAGE = new BundleResourceImageReference(m_parent.getApplicationInstance().getUserImage(), bundle);

		// Create the image label.

		final Label image = new Label(new BundleResourceImageReference(USER_IMAGE.getResource(), new Extent(50),
		      new Extent(50), bundle));

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
				//addUser(m_userName.getText());
				createUser(m_userName.getText());
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

	public void initializedSimulatedRooms() {
		SimulationManager simulationManager = m_parent.getApplicationInstance().getSimulationManager();
		Set<String> envs = simulationManager.getEnvironments();
		rooms.addAll(envs);
	}
	
	
	private synchronized void createUser(String userName) {
		final String normalizedName = userName.trim();
		if (normalizedName.isEmpty()) {
			showErrorWindow("The name of the user is empty.");
			return;
		} else if (m_users.containsKey(normalizedName)) {
			showErrorWindow("The user \"" + normalizedName + "\"already exists.");
			return;
		}
		m_parent.getApplicationInstance().getSimulationManager().addUser(normalizedName);
	}
	
	public void addUser(final String userName) {
		
		final UserEntry entry = new UserEntry();
		entry.name = userName;
		entry.label = new Label(userName);
		// Create the location selection menu.
		final DefaultMenuModel model = new DefaultMenuModel();

		//Set<String> rooms = roomPositions.keySet();
		for (String room : rooms) {
			model.addItem(new DefaultOptionModel(room, room, null));
		}

		final DefaultMenuSelectionModel menuSelect = new DefaultMenuSelectionModel();
		entry.locationMenu = new DropDownMenu(model, menuSelect);
		menuSelect.setSelectedId(OUT_SIDE);
		//entry.roomPosition = outsideRoomPosition;
		entry.location = UserPane.OUT_SIDE;
		entry.locationMenu.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1797893813768038434L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				m_parent.getApplicationInstance().getSimulationManager()
				      .setUserLocation(entry.name, e.getActionCommand().toLowerCase());
			}

		});
		// Create the remove button.
		entry.removeButton = new Button(new BundleResourceImageReference("/Remove.png", BaseHouseApplication.getBundle()));
		entry.removeButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1797893813768038434L;

			@Override
			public void actionPerformed(ActionEvent e) {
				m_parent.getApplicationInstance().getSimulationManager().removeUser(userName);				
			}
		});
		
		// Add the created components to the grid.
		m_grid.add(entry.label);
		m_grid.add(entry.locationMenu);
		m_grid.add(entry.removeButton);
		m_users.put(userName, entry);
	}

	public void removeUser(String userName) {
		// Remove the user in GUI
		UserEntry entry = m_users.remove(userName);
		m_grid.remove(entry.label);
		m_grid.remove(entry.locationMenu);
		m_grid.remove(entry.removeButton);
		if (entry.widget != null) {
			m_parent.getApplicationInstance().getHousePane().getChildContainer().remove(entry.dragSource);
		}
	   
   }
	
	public synchronized void moveUser(final String userName, final Position position) {
		SimulationManager simulationManager = m_parent.getApplicationInstance().getSimulationManager();
		final UserEntry entry = m_users.get(userName);
		String envId = simulationManager.getEnvironmentFromPosition(position);
		if (envId==null)
			envId = OUT_SIDE;
		moveUserWidget(entry, position, envId);		
		entry.locationMenu.getSelectionModel().setSelectedId(envId);
	}


	
	private void moveUserWidget(final UserEntry entry, final Position position, String location) {
		

		
		m_parent.getApplicationInstance().getHousePane().getChildContainer().remove(entry.dragSource);
		
		if (position != null) {
			// Create the new image.
			entry.widget = new FloatingButton(position.x, position.y, USER_IMAGE, entry.name);

			// add support of drag and drop
			final FloatingButtonDragSource dragSource = new FloatingButtonDragSource(entry.widget);
			dragSource.setBackground(Color.YELLOW);
			dragSource.addDropTarget(HousePane.HOUSE_PANE_RENDER_ID);
			dragSource.addDropListener(new DropListener() {

				@Override
				public void dropPerformed(DropEvent event) {
					//RoomPosition newRoomPosition = getRoomPosition( new Position(event.getTargetX(), event.getTargetY()));
					m_parent.getApplicationInstance().getSimulationManager().setUserPosition(entry.name, new Position(event.getTargetX(), event.getTargetY()));
				}
			});

			entry.dragSource = dragSource;

			m_parent.getApplicationInstance().getHousePane().getChildContainer().add(entry.dragSource);
		}
		
		entry.location = location;
	}
	
	
	private void showErrorWindow(final String error) {
		final WindowPane window = new WindowPane();
		// Create the icon.
		final Label icon = new Label(new BundleResourceImageReference("/Error.png", BaseHouseApplication.getBundle()));
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
		private FloatingButton widget;
		//private RoomPosition roomPosition;
		private String location;
		private FloatingButtonDragSource dragSource;
	}

}
