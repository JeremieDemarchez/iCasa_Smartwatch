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
package fr.liglab.adele.icasa.application.device.web.common.impl.component;


import nextapp.echo.app.Button;
import nextapp.echo.app.Grid;
import nextapp.echo.app.Label;
import nextapp.echo.app.Panel;
import nextapp.echo.app.RadioButton;
import nextapp.echo.app.TextField;
import nextapp.echo.app.button.ButtonGroup;
import nextapp.echo.app.event.ActionEvent;
import nextapp.echo.app.event.ActionListener;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public class TimeSelect extends Panel {

    /**
     * @Generated
     */
    private static final long serialVersionUID = -2925111468209982203L;

    private final TextField m_hour;
    private final TextField m_minute;
    private final RadioButton m_pmButton;

    public TimeSelect(final int initialHour, final int initialMinute, boolean pm) {

        m_hour = new TextField();
        m_hour.setMaximumLength(2);
        m_hour.setText(Integer.toString(initialHour));
        m_hour.setEditable(false);

        m_minute = new TextField();
        m_minute.setMaximumLength(2);
        m_minute.setText(Integer.toString(initialMinute));
        m_minute.setEditable(false);

        final Button hourPlus = new Button("+");
        hourPlus.addActionListener(new ActionListener() {
            private static final long serialVersionUID = -6745938960564071276L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                int value = Integer.parseInt(m_hour.getText());
                if (value < 11) {
                    value++;
                } else {
                    value = 0;
                }
                m_hour.setText(Integer.toString(value));
            }
        });

        final Button hourMinus = new Button("-");
        hourMinus.addActionListener(new ActionListener() {
            private static final long serialVersionUID = -6745938960564071276L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                int value = Integer.parseInt(m_hour.getText());
                if (value > 0) {
                    value--;
                } else {
                    value = 11;
                }
                m_hour.setText(Integer.toString(value));
            }
        });

        final Button minutePlus = new Button("+");
        minutePlus.addActionListener(new ActionListener() {
            private static final long serialVersionUID = -6745938960564071276L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                int value = Integer.parseInt(m_minute.getText());
                if (value < 59) {
                    value++;
                } else {
                    value = 0;
                }
                m_minute.setText(Integer.toString(value));
            }
        });

        final Button minuteMinus = new Button("-");
        minuteMinus.addActionListener(new ActionListener() {
            private static final long serialVersionUID = -6745938960564071276L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                int value = Integer.parseInt(m_minute.getText());
                if (value > 0) {
                    value--;
                } else {
                    value = 59;
                }
                m_minute.setText(Integer.toString(value));
            }
        });
        
        final ButtonGroup amPmGroup = new ButtonGroup();
        final RadioButton amButton = new RadioButton("AM");
        amButton.setGroup(amPmGroup);
        m_pmButton = new RadioButton("PM");
        m_pmButton.setGroup(amPmGroup);
        if (pm) {
            m_pmButton.setSelected(true);
        } else {
            amButton.setSelected(true);
        }
        
        final Grid grid = new Grid(3);
        grid.add(hourPlus);
        grid.add(new Label());
        grid.add(minutePlus);
        grid.add(m_hour);
        grid.add(new Label(":"));
        grid.add(m_minute);
        grid.add(hourMinus);
        grid.add(new Label());
        grid.add(minuteMinus);
        grid.add(amButton);
        grid.add(new Label());
        grid.add(m_pmButton);
        add(grid);
    }

    public int getHour() {
        return Integer.parseInt(m_hour.getText());
    }
    
    public int getMinute() {
        return Integer.parseInt(m_minute.getText());
    }
    
    public boolean isPm() {
        return m_pmButton.isSelected();
    }

}
