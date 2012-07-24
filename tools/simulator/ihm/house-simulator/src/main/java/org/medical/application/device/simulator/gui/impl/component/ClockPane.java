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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import nextapp.echo.app.Button;
import nextapp.echo.app.ContentPane;
import nextapp.echo.app.Extent;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Insets;
import nextapp.echo.app.Label;
import nextapp.echo.app.ResourceImageReference;
import nextapp.echo.app.SelectField;
import nextapp.echo.app.TextField;
import nextapp.echo.app.WindowPane;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;
import nextapp.echo.app.event.WindowPaneEvent;
import nextapp.echo.app.event.WindowPaneListener;
import nextapp.echo.app.layout.GridLayoutData;
import nextapp.echo.app.list.DefaultListModel;
import nextapp.echo.extras.app.CalendarSelect;

import org.medical.application.device.web.common.impl.BaseHouseApplication;
import org.medical.application.device.web.common.util.BundleResourceImageReference;
import org.medical.clock.api.Clock;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public class ClockPane extends ContentPane {

	/**
	 * @Generated
	 */
	private static final long serialVersionUID = -6120148194109048391L;

	private static final String NO_CLOCK_TEXT = "??? Unknow ???";

	public static ResourceImageReference CLOCK_IMAGE = new BundleResourceImageReference("/Clock.png", BaseHouseApplication.getBundle());

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM yyyy KK : mm : ss a");

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("KK : mm : ss a");

	public static Extent ICON_SIZE = new Extent(25);

	private final SimulatorActionPane m_parent;

	private final ScheduledExecutorService m_executor = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture<?> m_scheduledTask;

	private Clock m_clock;

	private final TextField m_date;
	private final TextField m_time;
	private final TextField m_timeFactor;
	// private final SelectField m_timeFactor;
	private final Button m_increaseTimeFactor;
	private final Button m_decreaseTimeFactor;
	private final Button m_setTimeAndDate;
	private final SelectField m_scriptList;
	private final SelectField m_scenarioList;
	
	public ClockPane(final SimulatorActionPane parent) {
		m_parent = parent;
		// Create the image label.
		final Label image = new Label(CLOCK_IMAGE);
		final GridLayoutData imageLayout = new GridLayoutData();
		imageLayout.setRowSpan(2);
		imageLayout.setColumnSpan(2);
		image.setLayoutData(imageLayout);

		ScriptActionListener actionListener = new ScriptActionListener();
		// Create the date and time controls
		m_date = new TextField();
		m_date.setEditable(false);
		m_date.setText(NO_CLOCK_TEXT);
		m_time = new TextField();
		m_time.setEditable(false);
		m_date.setText("hh:mm:ss");
		// Create the time factor labels and buttons.

		// String[] factors = {"60", "180", "360", "720", "1440", "2880", "5760"};
		m_timeFactor = new TextField();
		m_timeFactor.setEditable(false);
		m_timeFactor.setText(NO_CLOCK_TEXT);

		
		m_increaseTimeFactor = new Button("+");
		m_increaseTimeFactor.setActionCommand("IncreaseFactor");
		m_increaseTimeFactor.addActionListener(actionListener);

		m_decreaseTimeFactor = new Button("-");
		m_decreaseTimeFactor.setActionCommand("DecreaseFactor");
		m_decreaseTimeFactor.addActionListener(actionListener);

		// Create the button used to set the time and date.
		m_setTimeAndDate = new Button("Date Settings");
		m_setTimeAndDate.setActionCommand("SetDate");

		m_setTimeAndDate.addActionListener(actionListener);
		m_setTimeAndDate.setEnabled(false);

		m_scriptList = new SelectField(new DefaultListModel(m_parent.getApplicationInstance().getScriptList().toArray()));
		


		
		final Button refreshScript = new Button("Refresh");
		refreshScript.setActionCommand("RefreshScriptList");
		refreshScript.addActionListener(actionListener);
      
		
		final Button startScript = new Button("Start Script");
		startScript.setActionCommand("ExecuteScript");
		startScript.addActionListener(actionListener);

		final Button stopScript = new Button("Stop Script");
		stopScript.setActionCommand("StopScript");
		stopScript.addActionListener(actionListener);

		m_scenarioList = new SelectField(new DefaultListModel(m_parent.getApplicationInstance().getScenarioList().toArray()));
		
		final Button refreshScenario = new Button("Refresh");
		refreshScenario.setActionCommand("RefreshScenarioList");
		refreshScenario.addActionListener(actionListener);
		
		final Button installScenario = new Button("Install Scenario");
		installScenario.setActionCommand("InstallScenario");
		installScenario.addActionListener(actionListener);
		
		final Button saveButton = new Button("Save Scenario");
		saveButton.setActionCommand("SaveScenario");
		saveButton.addActionListener(actionListener);

		// Create the grid and add all components.
		
		final Grid clockGrid = new Grid(4);
		clockGrid.setInsets(new Insets(3));
		
		clockGrid.add(new Label("Date and Hour: "));
		clockGrid.add(m_date);
		clockGrid.add(m_time);
		clockGrid.add(m_setTimeAndDate);
		clockGrid.add(new Label("Time Factor : "));
		clockGrid.add(m_timeFactor);
		clockGrid.add(m_increaseTimeFactor);
		clockGrid.add(m_decreaseTimeFactor);
		
		
		final Grid scriptGrid = new Grid(5);
		scriptGrid.setInsets(new Insets(3));
		scriptGrid.add(new Label("Simulation Scripts : "));
		scriptGrid.add(m_scriptList);
		scriptGrid.add(refreshScript);
		scriptGrid.add(startScript);
		scriptGrid.add(stopScript);
		scriptGrid.add(new Label("iCasa Scenarios: "));
		scriptGrid.add(m_scenarioList);
		scriptGrid.add(refreshScenario);
		scriptGrid.add(installScenario);
		scriptGrid.add(saveButton);
		
		
		final Grid globalGrid = new Grid(1);					
		globalGrid.setInsets(new Insets(10));
		
		globalGrid.add(clockGrid);
		globalGrid.add(scriptGrid);
		

		add(globalGrid);
	}

	public synchronized void setClock(final Clock clock) {
		m_clock = clock;
		updateClockInfo();
		if (m_clock != null) {
			m_increaseTimeFactor.setEnabled(true);
			m_decreaseTimeFactor.setEnabled(true);
			m_setTimeAndDate.setEnabled(true);
			startUpdaterTask();
		} else {
			stopUpdaterTask();
			m_increaseTimeFactor.setEnabled(false);
			m_decreaseTimeFactor.setEnabled(false);
			m_setTimeAndDate.setEnabled(false);
		}
	}

	private void updateClockInfo() {
		if (m_clock != null) {
			long date = m_clock.currentTimeMillis();
			final Date now = new Date(date);
			m_date.setText(DATE_FORMAT.format(now));
			m_time.setText(TIME_FORMAT.format(now));
			m_timeFactor.setText(Integer.toString(m_clock.getFactor()) + "x");
		} else {
			m_date.setText(NO_CLOCK_TEXT);
			m_time.setText(NO_CLOCK_TEXT);
			m_timeFactor.setText(NO_CLOCK_TEXT);
		}
	}

	private synchronized void openTimeAndDateSettings() {
		final WindowPane window = new WindowPane();
		// Get the current time and date.
		final Date date = new Date(m_clock.currentTimeMillis());
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int hours = calendar.get(Calendar.HOUR);
		final int minutes = calendar.get(Calendar.MINUTE);
		final boolean isPm = (calendar.get(Calendar.AM_PM) == Calendar.PM);
		// Create the date selector.
		final CalendarSelect dateSelect = new CalendarSelect(date);
		// Create the time selector.
		final TimeSelect timeSelect = new TimeSelect(hours, minutes, isPm);
		// Create the confirmation button.
		final Button okButton = new Button("OK");
		okButton.addActionListener(new ActionListener() {
			private static final long serialVersionUID = -8941340231887074684L;

			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (ClockPane.this) {
					// Set the clock date.
					if (m_clock != null) {
						final Calendar newDate = Calendar.getInstance();
						newDate.setTime(dateSelect.getDate());
						newDate.set(Calendar.HOUR, timeSelect.getHour());
						newDate.set(Calendar.MINUTE, timeSelect.getMinute());
						if (timeSelect.isPm()) {
							newDate.set(Calendar.AM_PM, Calendar.PM);
						} else {
							newDate.set(Calendar.AM_PM, Calendar.AM);
						}
						m_clock.setStartDate(newDate.getTimeInMillis());
					}
				}
				window.userClose();
			}
		});
		final GridLayoutData okButtonLayout = new GridLayoutData();
		okButtonLayout.setColumnSpan(2);
		okButton.setLayoutData(okButtonLayout);
		// Create the grid.
		final Grid grid = new Grid(2);
		grid.add(dateSelect);
		grid.add(timeSelect);
		grid.add(okButton);
		// Populate the window.
		window.setTitle("Time and date settings");
		window.setIcon(new BundleResourceImageReference(ClockPane.CLOCK_IMAGE.getResource(), ICON_SIZE, ICON_SIZE, BaseHouseApplication.getBundle()));
		window.setModal(true);
		window.setMaximizeEnabled(false);
		window.setDefaultCloseOperation(WindowPane.DISPOSE_ON_CLOSE);
		window.setClosable(true);
		window.add(grid);
		// It seems that the time and date updater task obstructs the selection
		// of the month. So the updater task is stopped until the user has
		// selected new time and dates.
		stopUpdaterTask();
		window.addWindowPaneListener(new WindowPaneListener() {
			private static final long serialVersionUID = 1299314472714598409L;

			@Override
			public void windowPaneClosing(final WindowPaneEvent e) {
				startUpdaterTask();
			}
		});
		m_parent.getApplicationInstance().getWindow().getContent().add(window);
	}

	public synchronized void startUpdaterTask() {
		if (m_clock != null && m_scheduledTask == null) {
			// Schedule date & time updater task.
			m_scheduledTask = m_executor.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					m_parent.getApplicationInstance().enqueueTask(new Runnable() {
						@Override
						public void run() {
							synchronized (ClockPane.this) {
								updateClockInfo();
							}
						}
					});
				}
			}, 0, 1000, TimeUnit.MILLISECONDS);
		}
	}

	public synchronized void stopUpdaterTask() {
		if (m_scheduledTask != null) {
			// Cancel updater task.
			m_scheduledTask.cancel(false);
			m_scheduledTask = null;
		}
	}

	@Override
	public void dispose() {
		m_executor.shutdownNow();
		super.dispose();
	}

	/**
	 * Update the script list in the client side
	 */
	private void updateScriptList() {
		if (m_scriptList != null) {
			m_scriptList.setModel(new DefaultListModel());
			m_scriptList.setModel(new DefaultListModel(m_parent.getApplicationInstance().getScriptList().toArray()));
		}
	}
	
	private void updateScenarioList() {
		if (m_scenarioList != null) {
			m_scenarioList.setModel(new DefaultListModel());
			m_scenarioList.setModel(new DefaultListModel(m_parent.getApplicationInstance().getScenarioList().toArray()));
		}
	}


	class ScriptActionListener implements ActionListener {

		/**
       * 
       */
		private static final long serialVersionUID = -4000421223473540692L;

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("ExecuteScript")) {
				String scriptName = (String) m_scriptList.getSelectedItem();
				if (scriptName != null)
					m_parent.getApplicationInstance().executeScript(scriptName);
			} else if (command.equals("StopScript")) {
				m_parent.getApplicationInstance().stopScript();
			} else if (command.equals("RefreshScriptList")) {
				updateScriptList();
			} else if (command.equals("RefreshScenarioList")) {
				updateScenarioList();				
			} else if (command.equals("InstallScenario")) {
				String scenarioName = (String) m_scenarioList.getSelectedItem();
				if (scenarioName != null)
					m_parent.getApplicationInstance().installScenario(scenarioName);		
			} else if (command.equals("SetDate")) {
				openTimeAndDateSettings();
			} else if (command.equals("IncreaseFactor")) {
				synchronized (ClockPane.this) {
					if (m_clock == null) {
						return;
					}
					int factor = m_clock.getFactor();
					factor++;
					m_clock.setFactor(factor);
					m_timeFactor.setText(Integer.toString(factor) + "x");
				}
			} else if (command.equals("DecreaseFactor")) {
				synchronized (ClockPane.this) {
					if (m_clock == null) {
						return;
					}
					int factor = m_clock.getFactor();
					factor--;
					if (factor > 0) {
						m_clock.setFactor(factor);
						m_timeFactor.setText(Integer.toString(factor) + "x");
					}
				}
			} else if (command.equals("SaveScenario")) {
				m_parent.getApplicationInstance().saveSimulationEnvironment();
			}

		}

	}

}
