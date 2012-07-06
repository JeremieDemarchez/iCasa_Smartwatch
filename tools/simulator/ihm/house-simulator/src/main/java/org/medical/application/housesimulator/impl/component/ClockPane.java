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
package org.medical.application.housesimulator.impl.component;

import static org.medical.application.housesimulator.impl.component.ActionPane.ICON_SIZE;

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

	private static final String NO_CLOCK_TEXT = "???";

	public static ResourceImageReference CLOCK_IMAGE = new ResourceImageReference("/Clock.png");

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEE d MMMM yyyy");

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("KK : mm : ss a");

	private final ActionPane m_parent;

	private final ScheduledExecutorService m_executor = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture<?> m_scheduledTask;

	private Clock m_clock;

	private final TextField m_date;
	private final TextField m_time;
	private final TextField m_timeFactor;
	private final Button m_increaseTimeFactor;
	private final Button m_decreaseTimeFactor;
	private final Button m_setTimeAndDate;
	private final SelectField scriptList;

	public ClockPane(final ActionPane parent) {
		m_parent = parent;
		// Create the image label.
		final Label image = new Label(CLOCK_IMAGE);
		final GridLayoutData imageLayout = new GridLayoutData();
		imageLayout.setRowSpan(2);
		imageLayout.setColumnSpan(2);
		image.setLayoutData(imageLayout);
		// Create the date and time labels
		final Label date = new Label("Date : ");
		m_date = new TextField();
		m_date.setEditable(false);
		m_date.setText(NO_CLOCK_TEXT);
		final Label time = new Label("Time : ");
		m_time = new TextField();
		m_time.setEditable(false);
		m_time.setText(NO_CLOCK_TEXT);
		// Create the time factor labels and buttons.
		final Label timeFactor = new Label("Time factor : ");
		m_timeFactor = new TextField();
		m_timeFactor.setEditable(false);
		m_timeFactor.setText(NO_CLOCK_TEXT);
		m_increaseTimeFactor = new Button("+");
		m_increaseTimeFactor.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 1351306577271441289L;

			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (ClockPane.this) {
					if (m_clock == null) {
						return;
					}
					int factor = m_clock.getFactor();
					factor++;
					m_clock.setFactor(factor);
					m_timeFactor.setText(Integer.toString(factor) + "x");
				}
			}
		});
		m_increaseTimeFactor.setEnabled(false);
		m_decreaseTimeFactor = new Button("-");
		m_decreaseTimeFactor.addActionListener(new ActionListener() {
			private static final long serialVersionUID = -6402221064024852626L;

			@Override
			public void actionPerformed(ActionEvent e) {
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
			}
		});
		m_decreaseTimeFactor.setEnabled(false);
		// Create the button used to set the time and date.
		m_setTimeAndDate = new Button("Date Settings");
		m_setTimeAndDate.addActionListener(new ActionListener() {
			private static final long serialVersionUID = 8687195683650058463L;

			@Override
			public void actionPerformed(ActionEvent e) {
				openTimeAndDateSettings();
			}
		});
		m_setTimeAndDate.setEnabled(false);
		// Create the grid and add all components.
		final Grid grid = new Grid(4);
		grid.setInsets(new Insets(3));
		// grid.s
		// grid.add(image);
		grid.add(date);
		grid.add(m_date);
		grid.add(time);
		grid.add(m_time);
		grid.add(timeFactor);
		grid.add(m_timeFactor);
		grid.add(m_decreaseTimeFactor);
		grid.add(m_increaseTimeFactor);

		//
		final GridLayoutData scriptListLayoutData = new GridLayoutData();
		scriptListLayoutData.setRowSpan(1);
		scriptListLayoutData.setColumnSpan(2);
		scriptList = new SelectField(m_parent.getApplicationInstance().getScriptFileList());
		scriptList.setLayoutData(scriptListLayoutData);
		grid.add(scriptList);

		final Button startScript = new Button("Start Script");
		startScript.setActionCommand("Execute");

		startScript.addActionListener(new ActionListener() {

			/**
          * 
          */
			private static final long serialVersionUID = 132654L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Execute")) {
					String scriptName = (String) scriptList.getSelectedItem();
					if (scriptName!=null)
						m_parent.getApplicationInstance().executeScript(scriptName);
				}
			}
		});

		grid.add(startScript);

		final Button stopScript = new Button("Stop Script");
		stopScript.setActionCommand("Execute");

		stopScript.addActionListener(new ActionListener() {

			/**
          * 
          */
			private static final long serialVersionUID = -4608158715335864583L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Execute")) {
					m_parent.getApplicationInstance().stopScript();
				}
			}
		});

		grid.add(stopScript);
		grid.add(m_setTimeAndDate);
		
		final Button saveButton = new Button("Save Simulation");
		saveButton.addActionListener(new ActionListener() {
			
			/**
          * 
          */
         private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				m_parent.getApplicationInstance().saveSimulationEnvironment();				
			}
		});
		
		grid.add(saveButton);
		
		add(grid);
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
		window.setIcon(new ResourceImageReference(ClockPane.CLOCK_IMAGE.getResource(), ICON_SIZE, ICON_SIZE));
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
	public void updateScriptList() {
		System.out.println("File List to be updated " + scriptList);
		if (scriptList != null) {
			System.out.println("Updating File List");
			scriptList.setModel(new DefaultListModel());
			scriptList.setModel(m_parent.getApplicationInstance().getScriptFileList());
		}

	}

}
